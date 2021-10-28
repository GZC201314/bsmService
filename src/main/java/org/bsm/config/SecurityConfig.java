package org.bsm.config;

/**
 * @author GZC
 * @create 2021-10-25 15:08
 * @desc security config file
 */
import org.bsm.handler.MyAuthenticationFailureHandler;
import org.bsm.handler.MyAuthenticationSucessHandler;
import org.bsm.service.impl.UserDetailServiceImpl;
import org.bsm.utils.smscode.SmsAuthenticationConfig;
import org.bsm.utils.smscode.SmsCodeFilter;
import org.bsm.utils.validateCode.ValidateCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Autowired
  private MyAuthenticationSucessHandler authenticationSucessHandler;

  @Autowired
  private MyAuthenticationFailureHandler authenticationFailureHandler;

  @Autowired
  private ValidateCodeFilter validateCodeFilter;

  @Autowired
  private SmsCodeFilter smsCodeFilter;

  @Autowired
  private SmsAuthenticationConfig smsAuthenticationConfig;

  @Autowired
  private UserDetailServiceImpl userDetailService;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    System.out.println("进入鉴权------");
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
            .authorizeRequests() // 授权配置 无需认证的请求路径
            .antMatchers("/toLogin","/userAdd",
                    "/login.html", "/code/image","/code/sms","/**/login.css").permitAll()
            .anyRequest()  // 所有请求
            .authenticated() // 都需要认证
            .and()
            .csrf().disable();

            // 将短信验证码认证配置加到 Spring Security 中
//            .apply(smsAuthenticationConfig);
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailService).passwordEncoder(new MyPasswordEncoder());
  }
}
