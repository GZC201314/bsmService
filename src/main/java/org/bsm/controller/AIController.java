package org.bsm.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.Authorize;
import org.bsm.entity.Pages;
import org.bsm.entity.Role;
import org.bsm.entity.User;
import org.bsm.pagemodel.AipFaceResult;
import org.bsm.pagemodel.PageMenu;
import org.bsm.pagemodel.PageUpload;
import org.bsm.service.impl.AIServiceImpl;
import org.bsm.service.impl.PagesServiceImpl;
import org.bsm.service.impl.RoleServiceImpl;
import org.bsm.service.impl.UserServiceImpl;
import org.bsm.utils.Constants;
import org.bsm.utils.RedisUtil;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @author GZC
 * @create 2021-11-09 21:01
 * @desc 百度智能控制类
 */
@Api(tags = "百度智能控制类")
@Slf4j
@RestController
@RequestMapping("/ai")
public class AIController {
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    /**
     * 上传文字识别图像
     */
    @Autowired
    AIServiceImpl aiService;
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    UserServiceImpl userService;
    @Autowired
    RoleServiceImpl roleService;

    @Autowired
    PagesServiceImpl pagesService;

    @ApiOperation("图像文字识别接口")
    @PostMapping(value = "ocr", consumes = "multipart/*", headers = "content-type=multipart/form-data")
    public ResponseResult<Object> orc(PageUpload pageUpload) {
        log.info("into the ocr function");
        try {
            String result = aiService.ocr(pageUpload);
            return Response.makeOKRsp("图片识别成功").setData(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Response.makeErrRsp("图片识别失败");
        }
    }

    /*, consumes = "multipart/*", headers = "content-type=multipart/form-data"*/
    @ApiOperation("人脸识别登录接口")
    @PostMapping(value = "faceLogin")
    public ResponseResult<Object> faceLogin(PageUpload pageUpload, HttpServletRequest request, HttpServletResponse response) {
        log.info("into the faceLogin function");
        try {
            AipFaceResult aipFaceResult = aiService.facelogin(pageUpload);
            if (aipFaceResult != null && aipFaceResult.getError_code() == 0) {
                // 根据人脸识别的结果查询本地是否有用户记录,如果有的话,直接登录
                String username = aipFaceResult.getResult().getUser_list().get(0).getUser_id();
                double score = aipFaceResult.getResult().getUser_list().get(0).getScore();
                // 如果在人脸库中找到了记录,并且匹配度达到了70%以上,允许该用户登录
                if (score >= 70) {
                    final Date expirationDate = new Date(System.currentTimeMillis() + Constants.EXPIRATION * 1000);
                    String token = Jwts.builder()
                            .setSubject(username)
                            .setIssuedAt(new Date())
                            .setExpiration(expirationDate)
                            .signWith(SignatureAlgorithm.HS512, Constants.SECRET)
                            .compact();
                    response.setHeader("token", token);
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("username", username);
                    User user = userService.getOne(queryWrapper);
                    if (user != null) {
                        /*接入spring security */
                        SecurityContext context = SecurityContextHolder.getContext();
                        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                        QueryWrapper<Role> roleWrapper = new QueryWrapper<>();
                        roleWrapper.eq("roleid", user.getRoleid());
                        Role role = roleService.getOne(roleWrapper);
                        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_" + role.getRolename());
                        authorities.add(simpleGrantedAuthority);
                        context.setAuthentication(new
                                UsernamePasswordAuthenticationToken(user, user.getPassword() + "&" + user.getSalt(), authorities));
                        request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", context);

                        redisUtil.del(user.getUsername());
                        redisUtil.hset(user.getUsername(), "token", token, 60 * 10);
                        redisUtil.hset(user.getUsername(), "role", role.getRolename(), 60 * 10);
                        redisUtil.hset(user.getUsername(), "isFaceValid", user.getIsfacevalid(), 60 * 10);


                        User reUser = new User();
                        reUser.setUsername(username);
                        reUser.setUsericon(user.getUsericon());
                        JSONObject reJson = new JSONObject();
                        reJson.put("userinfo", reUser);
                        JSONObject menuJson = new JSONObject();
                        Set<Object> authorizeds = redisUtil.sGet(role.getRolename());
                        Set<PageMenu> pageMenuSet = new HashSet<>();
                        List<PageMenu> parentList = new ArrayList<>();
                        Map<String, List<PageMenu>> map = new HashMap<>();
                        assert authorizeds != null;
                        for (Object authorized : authorizeds) {
                            Authorize authorize = (Authorize) authorized;
                            if (!StringUtils.hasText(authorize.getPagepath())) {
                                PageMenu pageMenu = new PageMenu();
                                QueryWrapper<Pages> queryWrapper1 = new QueryWrapper<>();
                                queryWrapper1.eq("pageid", authorize.getPageid());
                                Pages pages = pagesService.getOne(queryWrapper1);
                                BeanUtils.copyProperties(pages, pageMenu);
                                pageMenu.setId(pages.getPagekey());
                                pageMenu.setName(pages.getTitle());
                                pageMenu.setPath(pages.getPagepath());
                                parentList.add(pageMenu);
                            } else {
                                /*如果是二级菜单,找到他的父节点*/
                                PageMenu pageMenu = new PageMenu();
                                QueryWrapper<Pages> queryWrapper1 = new QueryWrapper<>();
                                queryWrapper1.eq("pageid", authorize.getPageid());
                                Pages pages = pagesService.getOne(queryWrapper1);
                                BeanUtils.copyProperties(pages, pageMenu);
                                pageMenu.setId(pages.getPagekey());
                                pageMenu.setName(pages.getTitle());
                                pageMenu.setPath(pages.getPagepath());
                                if (map.containsKey(pages.getParentkey())) {
                                    map.get(pages.getParentkey()).add(pageMenu);
                                } else {
                                    List<PageMenu> children = new ArrayList<PageMenu>();
                                    children.add(pageMenu);
                                    map.put(pages.getParentkey(), children);
                                }
                            }
                        }

                        for (PageMenu parent : parentList) {
                            parent.setChildren(map.get(parent.getId()));
                        }


                        /*在这边拼装menuJson*/
                        reJson.put("menulist", JSONObject.toJSON(parentList).toString());
                        return Response.makeOKRsp("人脸识别登录成功").setData(reJson);
                    } else {
                        Response.makeErrRsp("没有在数据库中找到你的记录,请先注册!");
                    }
                } else {
                    Response.makeErrRsp("没有在人脸库中找到你的记录,请先添加你的记录!");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Response.makeErrRsp("人脸识别登录失败");
    }

    @ApiOperation("人脸识别注册接口")
    @PostMapping(value = "faceReg", consumes = "multipart/*", headers = "content-type=multipart/form-data")
    public ResponseResult<Object> faceReg(PageUpload pageUpload) {
        log.info("into the faceReg function");
        try {
            AipFaceResult aipFaceResult = aiService.faceReg(pageUpload);
            if (aipFaceResult != null && aipFaceResult.getError_code() == 0) {
                return Response.makeOKRsp("人脸识别注册成功");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return Response.makeErrRsp("人脸识别注册失败");
        }
        return Response.makeErrRsp("人脸识别注册失败");
    }
}