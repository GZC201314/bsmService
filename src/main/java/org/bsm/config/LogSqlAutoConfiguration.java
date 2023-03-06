package org.bsm.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.bsm.interceptor.ParameterInterceptorPlugin;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnClass({SqlSessionFactory.class})
@Configuration
public class LogSqlAutoConfiguration {
 public LogSqlAutoConfiguration() {
 }

 @Bean
 @ConditionalOnProperty(name = {"log.printSql"}, havingValue = "true", matchIfMissing = true)
 public ParameterInterceptorPlugin getLogSqlHandler() {
     return new ParameterInterceptorPlugin(true);
 }
}
