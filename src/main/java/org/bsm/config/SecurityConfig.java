package org.bsm.config;

/**
 * @author GZC
 * @create 2021-10-25 15:08
 * @desc security config file
 */

import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.Authorize;
import org.bsm.handler.MyAuthenticationFailureHandler;
import org.bsm.handler.MyAuthenticationSucessHandler;
import org.bsm.service.impl.AuthorizeServiceImpl;
import org.bsm.service.impl.UserDetailServiceImpl;
import org.bsm.utils.RedisUtils;
import org.bsm.utils.validateCode.ValidateCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyAuthenticationSucessHandler authenticationSucessHandler;

    @Autowired
    private MyAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private ValidateCodeFilter validateCodeFilter;


    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private AuthorizeServiceImpl authorizeService;

    @Autowired
    private RedisUtils redisUtils;

    /*设置记住密码*/
    @Autowired
    private DataSource dataSource;

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        /*在启动的时候创建表*/
//        jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }

    @Override

    protected void configure(HttpSecurity http) throws Exception {
        log.info("进入鉴权配置------");
        /*在这边查询数据库进行角色鉴权,然后把鉴权信息放到redis中*/
        List<Authorize> authorizes = authorizeService.list();
        Map<String, List<String>> authorizationMap = new HashMap<>();
        for (Authorize authorize :
                authorizes) {
            if (!authorizationMap.containsKey(authorize.getRolename())) {
                List<String> pages = new ArrayList<>();
                pages.add(authorize.getPagepath());
                authorizationMap.put(authorize.getRolename(), pages);
            } else {
                authorizationMap.get(authorize.getRolename()).add(authorize.getPagepath());
            }
            http.authorizeRequests().antMatchers(authorize.getPagepath()).hasRole(authorize.getRolename());
        }
        for (String key :
                authorizationMap.keySet()) {
            redisUtils.del(key);
            redisUtils.lSet(key, authorizationMap.get(key));
        }
        http.exceptionHandling().accessDeniedPage("/noauth");
        // 添加验证码校验过滤器
        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                // 添加短信验证码校验过滤器
//            .addFilterBefore(smsCodeFilter,UsernamePasswordAuthenticationFilter.class)
                .formLogin() // 表单登录
                // http.httpBasic() // HTTP Basic
                // 登录跳转 URL
                .loginPage("/toLogin")
                // 处理表单登录 URL
                .loginProcessingUrl("/login")
                // 处理登录成功
                .successHandler(authenticationSucessHandler)
                // 处理登录失败
                .failureHandler(authenticationFailureHandler)
                .and()
                .authorizeRequests()
                // 授权配置 无需认证的请求路径
                .antMatchers("/toLogin", "/userAdd",
                        "/login.html", "/code/image", "/code/sms", "/**/login.css", "**/*.js").permitAll()
                .anyRequest()  // 所有请求
                .authenticated() // 都需要认证
                .and()
                /*配置记住我*/
                .rememberMe().tokenRepository(persistentTokenRepository()).tokenValiditySeconds(3600 * 24 * 14).userDetailsService(userDetailService)
        ;
        http.csrf().disable();
        /*设置登录注销*/
        http.logout().logoutUrl("/logout");
        // 将短信验证码认证配置加到 Spring Security 中
//            .apply(smsAuthenticationConfig);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService).passwordEncoder(new MyPasswordEncoder());
    }
}
