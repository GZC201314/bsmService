package org.bsm.task.Factory;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.AdaptableJobFactory;

/**
 * 解决 quartz Job任务无法注入的问题
 * 原因是定时任务的 Job 对象实例化的过程是通过 Quartz 内部自己完成的，
 * 但是我们通过 Spring 进行注入的 Bean 却是由 Spring 容器管理的，
 * Quartz 内部无法感知到 Spring 容器管理的 Bean，所以没有办法在创建 Job 的时候就给装配进去。
 */
@Configuration
public class BsmJobFactory extends AdaptableJobFactory {
    @Autowired
    private AutowireCapableBeanFactory autowireCapableBeanFactory;
    @Override
    protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
        Object jobInstance = super.createJobInstance(bundle);
        autowireCapableBeanFactory.autowireBean(jobInstance);
        return jobInstance;
    }
}
