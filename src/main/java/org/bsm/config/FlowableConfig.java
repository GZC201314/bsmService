package org.bsm.config;

import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.AbstractEngineConfiguration;
import org.flowable.common.engine.impl.EngineConfigurator;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author GZC
 * @create 2022-02-23 15:13
 * @desc Flowable 配置类
 */
@Slf4j
@Configuration
public class FlowableConfig implements EngineConfigurator {
    private static AtomicBoolean initialized = new AtomicBoolean(false);
    @Override
    public void beforeInit(AbstractEngineConfiguration abstractEngineConfiguration) {
        if (initialized.compareAndSet(false, true)){
            DataSource dataSource = abstractEngineConfiguration.getDataSource();
            if (dataSource instanceof TransactionAwareDataSourceProxy){
                dataSource = ((TransactionAwareDataSourceProxy) dataSource).getTargetDataSource();
            }
            if (dataSource instanceof DynamicRoutingDataSource){
                DataSource flowable = ((DynamicRoutingDataSource) dataSource).getDataSource("flowable");
                abstractEngineConfiguration.setDataSource(flowable);
            }
            log.info("切换flower数据源！");
        }
    }

    @Override
    public void configure(AbstractEngineConfiguration abstractEngineConfiguration) {

    }

    @Override
    public int getPriority() {
        return 0;
    }

//    @Value("${flowable.datasource.url}")
//    private String url;
//
//    @Value("${flowable.datasource.username}")
//    private String username;
//    @Value("${flowable.datasource.password}")
//    private String password;
//    @Value("${flowable.datasource.driver-class-name}")
//    private String driver;
//
//    @Bean("processEngine")
//    public ProcessEngine getProcessEngine() {
//        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
//                .setJdbcUrl(url)
//                .setJdbcUsername(username)
//                .setJdbcPassword(password)
//                .setJdbcDriver(driver)
//                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
//
//        return cfg.buildProcessEngine();
//    }

}
