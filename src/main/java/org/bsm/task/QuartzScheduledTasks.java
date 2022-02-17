//package org.bsm.task;
//
//import lombok.extern.slf4j.Slf4j;
//import org.quartz.*;
//import org.springframework.beans.factory.annotation.Configurable;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.quartz.QuartzJobBean;
//import org.springframework.stereotype.Component;
//
//@Component
//@Configurable
//@EnableScheduling
//@Slf4j
//@PersistJobDataAfterExecution
//@DisallowConcurrentExecution
//public class QuartzScheduledTasks extends QuartzJobBean {
//
//
//    @Override
//    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
//        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
//        String name = (String) jobDataMap.get("name");
//        log.info(name);
//    }
//}