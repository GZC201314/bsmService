package org.bsm.filter;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.CurUser;
import org.bsm.utils.RedisUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author GZC
 * @create 2021-12-15 19:07
 * @desc 添加公共参数过滤器
 */
@Slf4j
@Component
@WebFilter(urlPatterns = "/*", filterName = "AddParamFilter")
public class AddParamFilter implements Filter {

    @Resource
    RedisUtil redisUtil;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        /*TODO 添加公共的参数是否应该*/
        HttpServletRequest httpRequest = (HttpServletRequest)servletRequest;
        HttpSession session = httpRequest.getSession();
        Map<Object, Object> hmget;
        CurUser curUser = null;
        if (session != null) {
            String id = session.getId();
            hmget = redisUtil.hmget(id);
            curUser = BeanUtil.fillBeanWithMap(hmget, new CurUser(), false);
        }

        if (curUser != null) {
            servletRequest.setAttribute("curUser",curUser);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }


    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
