package org.bsm.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

/**
 * @author GZC
 * @create 2021-11-03 13:19
 * @desc swagger 的配置类
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    Profiles profiles = Profiles.of("dev", "test");

    @Bean
    public Docket getDocket(Environment environment) {
        boolean flag = environment.acceptsProfiles(profiles);
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(flag)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.bsm.controller"))
                .build();
    }

    private ApiInfo apiInfo() {
        // 作者信息
        Contact contact = new Contact("GZC", "https://www.bookstoremanager.top", "gzc201314@163.com");
        return new ApiInfo(
                "BSM API 文档",
                "BSM 是一个集成react和springboot的小项目.麻雀虽小,五脏俱全",
                "1.0.0",
                "https://www.bookstoremanager.top",
                contact,
                "Apache 2.0",
                "https://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList<>());
    }

}
