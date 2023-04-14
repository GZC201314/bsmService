package org.bsm.handler;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bsm.controller.AIController;
import org.bsm.pagemodel.PageMenu;
import org.bsm.service.impl.PagesServiceImpl;
import org.bsm.service.impl.RoleServiceImpl;
import org.bsm.service.impl.UserServiceImpl;
import org.bsm.utils.Constants;
import org.bsm.utils.RedisUtil;
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


        org.bsm.entity.User reUser = new org.bsm.entity.User();
        /*获取用户详细信息*/
        QueryWrapper<org.bsm.entity.User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        org.bsm.entity.User userInfo = userService.getOne(queryWrapper);

        String sessionId = request.getSession().getId();
        int sessionTimeout = Integer.parseInt((String) redisUtil.hget("bsm_config", "SESSION_TIMEOUT"));
        redisUtil.del(sessionId);
        redisUtil.hset(sessionId, "token", token, sessionTimeout);
        redisUtil.hset(sessionId, "username", userInfo.getUsername(), sessionTimeout);
        redisUtil.hset(sessionId, "useremail", userInfo.getEmailaddress(), sessionTimeout);
        redisUtil.hset(sessionId, "userid", userInfo.getUserid(), sessionTimeout);
        redisUtil.hset(sessionId, "role", roleName, sessionTimeout);
        redisUtil.hset(sessionId, "isFaceValid", userInfo.getIsfacevalid(), sessionTimeout);

        reUser.setUsername(user.getUsername());
        reUser.setUsericon(userInfo.getUsericon());
        reUser.setUserid(userInfo.getUserid());
        JSONObject reJson = new JSONObject();
        reJson.put("userinfo", reUser);
        Set<Object> authorizeds = redisUtil.sGet(roleName);
        List<PageMenu> parentList = new ArrayList<>();
        Map<String, List<PageMenu>> map = new HashMap<>();
        assert authorizeds != null;
        AIController.handleAuthorizedList(parentList, authorizeds, map, pagesService);


        /*在这边拼装menuJson*/
        reJson.put("menulist", JSONObject.toJSON(parentList).toString());
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(reJson.toJSONString());
        response.setHeader("token", token);
    }
}
