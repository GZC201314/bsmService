package org.bsm.config;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author GZC
 * @create 2022-02-16 14:00
 * @desc 异步定时任务配置类
 */
@Configuration
@Slf4j
public class AsyncScheduledTaskConfig {

    @Autowired
    DataSource dataSource;
    

    @Bean
    public Scheduler scheduler() {
        return schedulerFactoryBean().getScheduler();
    }

    /**
     * Details：定义quartz调度工厂
     */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean bean = new SchedulerFactoryBean();
        //设置调度器名

        bean.setSchedulerName("BSM_scheduler");
        /*设置数据源*/
        bean.setDataSource(dataSource);

        /*读取配置文件*/
        bean.setQuartzProperties(quartzProperties());

        /*设置调度器*/
        bean.setTaskExecutor(schedulerThreadPool());

        /*设置项目启动之后延迟执行*/
        bean.setStartupDelay(10);

        return bean;
    }

    /**
     * 读取 quartz 配置文件
     *
     * @return
     */
    @Bean
    public Properties quartzProperties() {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        /*读取配置文件*/
        try {
            propertiesFactoryBean.afterPropertiesSet();
            return propertiesFactoryBean.getObject();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * quartz 线程池配置
     *
     * @return
     */
    @Bean
    public Executor schedulerThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors());
        executor.setQueueCapacity(Runtime.getRuntime().availableProcessors());
        return executor;
    }

}
