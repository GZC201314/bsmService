package org.bsm.config;

import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author GZC
 * @create 2022-02-23 15:13
 * @desc Flowable 配置类
 */
@Configuration
public class FlowableConfig {

    @Value("${flowable.datasource.url}")
    private String url;

    @Value("${flowable.datasource.username}")
    private String username;
    @Value("${flowable.datasource.password}")
    private String password;
    @Value("${flowable.datasource.driver-class-name}")
    private String driver;

    @Bean("processEngine")
    public ProcessEngine getProcessEngine() {
        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl(url)
                .setJdbcUsername(username)
                .setJdbcPassword(password)
                .setJdbcDriver(driver)
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

        return cfg.buildProcessEngine();
    }

}
