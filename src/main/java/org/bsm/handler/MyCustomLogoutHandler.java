package org.bsm.handler;

import lombok.extern.slf4j.Slf4j;
import org.bsm.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author GZC
 * @create 2021-12-15 15:12
 * @desc 自定义登出处理类
 */
@Slf4j
@Component
public class MyCustomLogoutHandler implements LogoutHandler {

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication == null) {
            return;
        }
        User user = (User) authentication.getPrincipal();
        String username = user.getUsername();
        //清空redis中的session信息
        String sessionId = request.getSession().getId();
        redisUtil.del(sessionId);
    }
}
