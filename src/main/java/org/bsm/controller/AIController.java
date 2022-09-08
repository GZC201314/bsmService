package org.bsm.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.annotation.RefreshSession;
import org.bsm.annotation.StatisticsQPS;
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

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("图像文字识别接口")
    @PostMapping(value = "ocr", consumes = "multipart/*", headers = "content-type=multipart/form-data")
    public ResponseResult<Object> orc(PageUpload pageUpload, HttpServletResponse response) {
        log.info("into the ocr function");
        try {
            String result = aiService.ocr(pageUpload, response);
            return Response.makeOKRsp("图片识别成功").setData(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Response.makeErrRsp("图片识别失败");
        }
    }

    /*, consumes = "multipart/*", headers = "content-type=multipart/form-data"*/
    @StatisticsQPS
    @ApiOperation("人脸识别登录接口")
    @PostMapping(value = "faceLogin")
    public ResponseResult<Object> faceLogin(PageUpload pageUpload, HttpServletRequest request, HttpServletResponse response) {
        log.info("into the faceLogin function");
        try {
            AipFaceResult aipFaceResult = aiService.facelogin(pageUpload);
            if (aipFaceResult != null) {
                // 根据人脸识别的结果查询本地是否有用户记录,如果有的话,直接登录
                String username = aipFaceResult.getResult().getUser_list().get(0).getUser_id();
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

                    String sessionId = request.getSession().getId();
                    redisUtil.del(sessionId);
                    redisUtil.hset(sessionId, "token", token, 60 * 300);
                    redisUtil.hset(sessionId, "username", user.getUsername(), 60 * 300);
                    redisUtil.hset(sessionId, "role", role.getRolename(), 60 * 300);
                    redisUtil.hset(sessionId, "isFaceValid", false, 60 * 300);

                    JSONObject reJson = new JSONObject();
                    List<PageMenu> parentList = new ArrayList<>();

                    getAuthPages(username, user, role, reJson, parentList);


                    /*在这边拼装menuJson*/
                    reJson.put("menulist", JSONObject.toJSON(parentList).toString());
                    return Response.makeOKRsp("人脸识别登录成功").setData(reJson);
                } else {
                    Response.makeErrRsp("没有在数据库中找到你的记录,请先注册!");
                }
            } else {
                Response.makeErrRsp("没有在人脸库中找到你的记录,请先注册!");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return Response.makeErrRsp("人脸识别登录失败");
    }

    private void getAuthPages(String username, User user, Role role, JSONObject reJson, List<PageMenu> parentList) {
        User reUser = new User();
        reUser.setUsername(username);
        reUser.setUsericon(user.getUsericon());
        reJson.put("userinfo", reUser);
        Set<Object> authorizeds = redisUtil.sGet(role.getRolename());
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
                pageMenu.setOrderid(pages.getOrderid());
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
                    List<PageMenu> children = new ArrayList<>();
                    children.add(pageMenu);
                    map.put(pages.getParentkey(), children);
                }
            }
        }

        for (PageMenu parent : parentList) {
            parent.setChildren(map.get(parent.getId()));
        }

        parentList.sort(Comparator.comparingInt(PageMenu::getOrderid));
    }

    @StatisticsQPS
    @ApiOperation("人脸识别注册接口")
    @PostMapping(value = "faceRegister", consumes = "multipart/*", headers = "content-type=multipart/form-data")
    public ResponseResult<Object> faceRegister(PageUpload pageUpload, HttpServletRequest request) {
        log.info("into the faceRegister function");
        if (pageUpload.getFile() == null) {
            return Response.makeErrRsp("人脸注册，参数错误");
        }
        try {
            String sessionId = request.getSession().getId();
            pageUpload.setSessionId(sessionId);
            boolean aipFaceResult = aiService.faceReg(pageUpload);
            if (aipFaceResult) {
                return Response.makeOKRsp("人脸识别注册成功");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return Response.makeErrRsp("人脸识别注册失败");
        }
        return Response.makeErrRsp("人脸识别注册失败");
    }
}
