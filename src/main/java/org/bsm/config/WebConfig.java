//package org.bsm.config;
//
//import org.bsm.filter.AddParamFilter;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author GZC
// * @create 2021-12-15 19:02
// * @desc 过滤器配置类
// */
//@Configuration
//public class WebConfig {
//    @Bean
//    public FilterRegistrationBean<AddParamFilter> reqResFilter() {
//        FilterRegistrationBean<AddParamFilter> filterRegistrationBean = new FilterRegistrationBean<AddParamFilter>();
//
//        AddParamFilter addParamFilter = new AddParamFilter();
//
//        filterRegistrationBean.setFilter(addParamFilter);
//        //配置过滤规则
//        filterRegistrationBean.addUrlPatterns("/*");
//        //设置过滤器名称
//        filterRegistrationBean.setName("addParamFilter");
//        //执行次序
//        filterRegistrationBean.setOrder(1);
//
//        return filterRegistrationBean;
//    }
//}
