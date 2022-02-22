package org.bsm.task.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

/**
 * @author GZC
 * @create 2022-02-17 13:58
 * @desc 我的第一个Job任务
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
@Slf4j
public class MyFirshJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) {
        try {
            Thread.sleep(2000);
            /*获取定时任务的参数*/
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            log.info(context.getScheduler().getSchedulerInstanceId());
            log.info("taskname= {}", context.getJobDetail().getKey().getName());
            log.info("taskDataMap= {}", jobDataMap.get("key"));
            log.info("执行时间= {}", new Date());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
