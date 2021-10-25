package org.bsm.config;

/**
 * @author GZC
 * @create 2021-10-25 15:08
 * @desc security config file
 */
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests()
        .antMatchers("/")
        .permitAll()
        .antMatchers("level1")
        .hasRole("vip1")
        .antMatchers("level2")
        .hasRole("vip2")
        .antMatchers("level3")
        .hasRole("vip3");
    /*没有权限跳转到登录页面*/
    http.formLogin();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    super.configure(auth);
  }
}
