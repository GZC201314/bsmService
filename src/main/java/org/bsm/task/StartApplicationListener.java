package org.bsm.task;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * @author GZC
 * @create 2022-02-17 14:06
 * @desc 项目启动监听器 监听在容器启动后的时间
 */
@Slf4j
@Component
public class StartApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private Scheduler scheduler;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
//        try {
//            //设置触发器
//            TriggerKey triggerKey = new TriggerKey("BSMTrigger", "BSMGroup");
//            SchedulerContext context = scheduler.getContext();
//            Trigger trigger = scheduler.getTrigger(triggerKey);
//
//            JobDataMap jobDataMap = new JobDataMap();
//            jobDataMap.put("name", "BSM");
//            jobDataMap.put("sex", "man");
//            jobDataMap.put("isTest", true);
//
//            if (trigger == null) {
//                trigger = TriggerBuilder.newTrigger()
//                        .withIdentity(triggerKey)
//                        .withDescription("每十秒调度一次")
//                        //设置调度器，每十秒调度一次
//                        .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
//                        .build();
//                /*设置作业*/
//                JobDetail jobDetail = JobBuilder.newJob(MyFirshJob.class)
//                        .withIdentity("MyFirstJob", "BSMGroup")
//                        .setJobData(jobDataMap)
//                        .withDescription("This is My First Quartz Job.")
//                        .build();
//                scheduler.scheduleJob(jobDetail, trigger);
//                scheduler.start();
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }

    }
}
