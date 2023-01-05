package org.bsm;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author GZC
 * @create 2022-01-17 15:59
 * @desc 集成scala的测试类
 */
@Disabled
@Slf4j
public class TestFlowable {
    @Autowired
    ProcessEngine processEngine;

    /*
     *
     *
     *
     * */
    @Test
    public void testFlowableMenthod() {

//        String url = "jdbc:mysql://127.0.0.1:3306/flowable?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8";
//        String username = "root";
//        String password = "123456";
//        String driver = "com.mysql.cj.jdbc.Driver";
//        ProcessEngineConfiguration cfg = new StandaloneProcessEngineConfiguration()
//                .setJdbcUrl(url)
//                .setJdbcUsername(username)
//                .setJdbcPassword(password)
//                .setJdbcDriver(driver)
//                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
//
//        ProcessEngine processEngine = cfg.buildProcessEngine();
//
        RepositoryService repositoryService = processEngine.getRepositoryService();
        Deployment deploy = repositoryService.createDeployment().addClasspathResource("dpmn20.xml").deploy();

    }
}
