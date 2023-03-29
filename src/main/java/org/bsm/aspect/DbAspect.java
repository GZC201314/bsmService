// An highlighted block
package org.bsm.aspect;


import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Aspect
public class DbAspect {


    @Before("execution(* org.flowable.app.engine.AppEngineConfiguration.buildAppEngine())")
    public void before() {
        log.info("启用工作流数据源");
        DynamicDataSourceContextHolder.push("flowable");
    }

    @After("execution(* org.flowable.app.engine.AppEngineConfiguration.buildAppEngine())")
    public void after() {
        log.info("停用工作流数据源");
        DynamicDataSourceContextHolder.poll();
    }

}

