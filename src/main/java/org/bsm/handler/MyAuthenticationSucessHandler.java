package org.bsm.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bsm.entity.Authorize;
import org.bsm.entity.Pages;
import org.bsm.pagemodel.PageMenu;
import org.bsm.service.impl.PagesServiceImpl;
import org.bsm.service.impl.RoleServiceImpl;
import org.bsm.service.impl.UserServiceImpl;
import org.bsm.utils.Constants;
import org.bsm.utils.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Component
public class MyAuthenticationSucessHandler implements AuthenticationSuccessHandler {

    // private RequestCache requestCache = new HttpSessionRequestCache();

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    UserServiceImpl userService;
    @Autowired
    RoleServiceImpl roleService;

    @Autowired
    PagesServiceImpl pagesService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        User user = (User) authentication.getPrincipal();
        final Date expirationDate = new Date(System.currentTimeMillis() + Constants.EXPIRATION * 1000);
        String token = Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, Constants.SECRET)
                .compact();
        String roleName = "";
        if (user.getAuthorities().stream().findFirst().isPresent()) {
            roleName = user.getAuthorities().stream().findFirst().get().getAuthority();
            if (StringUtils.hasText(roleName)) {
                roleName = roleName.replace("ROLE_", "");
            }
        }
        String sessionId = request.getSession().getId();
        redisUtil.del(sessionId);
        redisUtil.hset(sessionId, "token", token, 60 * 300);
        redisUtil.hset(sessionId, "username", user.getUsername(), 60 * 300);
        redisUtil.hset(sessionId, "role", roleName, 60 * 300);
        redisUtil.hset(sessionId, "isFaceValid", false, 60 * 300);

        org.bsm.entity.User reUser = new org.bsm.entity.User();
        /*获取用户详细信息*/
        QueryWrapper<org.bsm.entity.User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        org.bsm.entity.User userInfo = userService.getOne(queryWrapper);
        reUser.setUsername(user.getUsername());
        reUser.setUsericon(userInfo.getUsericon());
        JSONObject reJson = new JSONObject();
        reJson.put("userinfo", reUser);
        JSONObject menuJson = new JSONObject();
        Set<Object> authorizeds = redisUtil.sGet(roleName);
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
        parentList.sort((o1, o2) -> o1.getOrderid() - o2.getOrderid());


        /*在这边拼装menuJson*/
        reJson.put("menulist", JSONObject.toJSON(parentList).toString());
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(reJson.toJSONString());
        response.setHeader("token", token);
    }
}
