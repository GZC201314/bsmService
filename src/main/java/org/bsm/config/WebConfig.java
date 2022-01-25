package org.bsm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author GZC
 * @create 2021-12-15 19:02
 * @desc 过滤器配置类
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer
                .addPathPrefix("bsmservice", c -> c.isAnnotationPresent(RestController.class));
    }

}
