package org.bsm.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import java.io.IOException;

/**
 * @author GZC
 * @create 2021-12-15 19:07
 * @desc 添加公共参数过滤器
 */
@Slf4j
public class AddParamFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //获取 filterRegistrationBean.addInitParameter("XXX","XXXX")设置的参数
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        /*TODO 添加公共的参数是否应该*/
        log.info("AddParamFilter is running.");
    }


    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
