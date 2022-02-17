package org.bsm.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.bsm.pagemodel.Page;
import org.bsm.pagemodel.PageObject;
import org.bsm.pagemodel.PageTask;
import org.bsm.service.ITaskService;
import org.bsm.task.job.MyFirshJob;
import org.bsm.utils.MyPageUtil;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author GZC
 * @create 2022-02-17 15:24
 * @desc 定时任务实现类
 */
@Slf4j
@Service
public class TaskServiceImpl implements ITaskService {

    @Autowired
    private Scheduler scheduler;

    /**
     * 获取分页的定时任务信息
     */
    @Override
    public PageObject<JSONObject> getAllTaskPage(PageTask pageTask) {
        List<JSONObject> taskList = new ArrayList<>();
        Page page = pageTask.getPage();
        Integer pageSize = page.getPageSize();
        Integer pageNo = page.getPage();

        /*获取所有的定时任务*/
        try {
            List<String> jobGroupNames = scheduler.getJobGroupNames();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (String jobGroupName :
                    jobGroupNames) {
                for (JobKey jobKey :
                        scheduler.getJobKeys(GroupMatcher.jobGroupEquals(jobGroupName))) {
                    JSONObject taskInfo = new JSONObject();
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    taskInfo.put("jobName", jobKey.getName());
                    taskInfo.put("jobGroup", jobKey.getGroup());
                    taskInfo.put("jobClass", jobDetail.getJobClass().getName());
                    taskInfo.put("jobDescription", jobDetail.getDescription());
                    List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobKey);
                    if (triggersOfJob.size() > 0) {
                        Trigger trigger = triggersOfJob.get(0);
                        TriggerKey triggerKey = trigger.getKey();
                        taskInfo.put("triggerName", triggerKey.getName());
                        taskInfo.put("triggerGroup", triggerKey.getGroup());
                        taskInfo.put("previousFireTime", simpleDateFormat.format(trigger.getPreviousFireTime()));
                        taskInfo.put("nextFireTime", simpleDateFormat.format(trigger.getNextFireTime()));
                        if (trigger instanceof CronTrigger) {
                            CronTrigger cronTrigger = (CronTrigger) trigger;
                            taskInfo.put("cron", cronTrigger.getCronExpression());
                        }
                    }
                    taskList.add(taskInfo);
                }
            }
            // 封装分页对象
            return MyPageUtil.getPageObject(taskList, pageSize, pageNo);
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }
        return new PageObject<>();
    }

    /**
     * 删除定时任务，支持批量删除
     */
    @Override
    public boolean deleteTasks(PageTask pageTask) {
        return false;
    }

    /**
     * 获取定时任务的详细信息
     */
    @Override
    public JSONObject getTaskInfo(PageTask pageTask) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSONObject taskInfo = new JSONObject();
        String jobName = pageTask.getJobKey();
        String jobGroup = pageTask.getGroup();
        taskInfo.put("jobName", jobName);
        taskInfo.put("jobGroup", jobGroup);
        JobKey jobKey = new JobKey(jobName, jobGroup);
        try {
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail != null) {
                taskInfo.put("jbClass", jobDetail.getJobClass());
                taskInfo.put("jobDescription", jobDetail.getDescription());
                taskInfo.put("jobDataMap", jobDetail.getJobDataMap());
                /*通过JobKey 来获取Trigger*/

                List<? extends Trigger> triggersOfJob = scheduler.getTriggersOfJob(jobKey);
                if (triggersOfJob.size() > 0) {
                    Trigger trigger = triggersOfJob.get(0);
                    TriggerKey triggerKey = trigger.getKey();
                    taskInfo.put("triggerName", triggerKey.getName());
                    taskInfo.put("triggerGroup", triggerKey.getGroup());
                    taskInfo.put("triggerDataMap", trigger.getJobDataMap());
                    taskInfo.put("triggerDescription", trigger.getDescription());
                    taskInfo.put("previousFireTime", simpleDateFormat.format(trigger.getPreviousFireTime()));
                    taskInfo.put("nextFireTime", simpleDateFormat.format(trigger.getNextFireTime()));
                    if (trigger instanceof CronTrigger) {
                        CronTrigger cronTrigger = (CronTrigger) trigger;
                        taskInfo.put("cron", cronTrigger.getCronExpression());
                    }
                }

            }
            return taskInfo;

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    /**
     * 新增定时任务
     */
    @Override
    public String addTask(PageTask pageTask) {

        //设置触发器
        TriggerKey triggerKey = new TriggerKey(pageTask.getTriggerName(), pageTask.getTriggerGroup());
        JobKey jobKey = new JobKey(pageTask.getJobKey(), pageTask.getGroup());
        try {
            Trigger trigger = scheduler.getTrigger(triggerKey);

            JobDataMap jobDataMap = new JobDataMap();
            Map<String, Object> jobDataMap1 = pageTask.getJobDataMap();
            Set<Map.Entry<String, Object>> entries = jobDataMap1.entrySet();
            for (Map.Entry<String, Object> entry : entries) {
                jobDataMap.put(entry.getKey(), entry.getValue());
            }

            if (trigger == null) {
                trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withDescription("每十秒调度一次")
                        //设置调度器，每十秒调度一次
                        .withSchedule(CronScheduleBuilder.cronSchedule("0/10 * * * * ?"))
                        .build();
                /*设置作业*/
                JobDetail jobDetail = JobBuilder.newJob(MyFirshJob.class)
                        .withIdentity(jobKey)
                        .setJobData(jobDataMap)
                        .withDescription("This is My First Quartz Job.")
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);
                scheduler.start();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return "";
    }

    /**
     * 关闭所有任务
     */
    @Override
    public boolean stopAllTask(PageTask pageTask) {
        return false;
    }

    /**
     * 启动所有任务
     */
    @Override
    public boolean startAllTask(PageTask pageTask) {
        return false;
    }
}
