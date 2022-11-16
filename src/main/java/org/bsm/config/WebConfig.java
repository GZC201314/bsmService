package org.bsm.config;

import org.bsm.interceptor.BlackIPInterceptor;
import org.bsm.utils.RedisUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author GZC
 * @create 2021-12-15 19:02
 * @desc 过滤器配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public RedisUtil getRedisUtil(){
        return new RedisUtil();
    }

    @Bean
    public HandlerInterceptor getBlackIPInterceptor(){
        return new BlackIPInterceptor(getRedisUtil());
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer
                .addPathPrefix("bsmservice", c -> c.isAnnotationPresent(RestController.class));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new BlackIPInterceptor(getRedisUtil())).addPathPatterns("/**").order(0);
    }

}
