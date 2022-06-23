package org.bsm.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.bsm.pagemodel.Page;
import org.bsm.pagemodel.PageObject;
import org.bsm.pagemodel.PageTask;
import org.bsm.pagemodel.Param;
import org.bsm.service.ITaskService;
import org.bsm.utils.Constants;
import org.bsm.utils.MyPageUtil;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

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
                        //获取当前触发器的状态
                        Trigger.TriggerState triggerState = scheduler.getTriggerState(triggerKey);
                        taskInfo.put("state", triggerState.name());
                        taskInfo.put("triggerName", triggerKey.getName());
                        taskInfo.put("triggerGroup", triggerKey.getGroup());
                        if (trigger.getPreviousFireTime() != null) {
                            taskInfo.put("previousFireTime", simpleDateFormat.format(trigger.getPreviousFireTime()));
                        }
                        if (trigger.getNextFireTime() != null) {
                            taskInfo.put("nextFireTime", simpleDateFormat.format(trigger.getNextFireTime()));
                        }
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
        return MyPageUtil.getPageObject(taskList, pageSize, pageNo);
    }

    /**
     * 删除定时任务，支持批量删除
     */
    @Override
    public boolean deleteTasks(PageTask pageTask) {
        String[] jobKeyArr = pageTask.getDelIds().split(",");
        boolean result = false;
        List<JobKey> jobKeyList = new ArrayList<>();
        for (String jobKeyString :
                jobKeyArr) {
            String[] jobKeyInfo = jobKeyString.split("\\.");
            String jobName = jobKeyInfo[0];
            String groupName = jobKeyInfo[1];
            JobKey jobKey = new JobKey(jobKeyString, groupName);
            jobKeyList.add(jobKey);
        }
        try {
            result = scheduler.deleteJobs(jobKeyList);
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }


        return result;
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
     * 校验定时任务Key
     *
     * @param pageTask
     */
    @Override
    public boolean validateJobKey(PageTask pageTask) {


        String[] jobKey = pageTask.getJobKey().split("\\.");


        String jobName = pageTask.getJobKey();
        String groupKey = jobKey[1];
        JobKey jobKey1 = new JobKey(jobName, groupKey);
        boolean checkExists = false;
        try {
            checkExists = scheduler.checkExists(jobKey1);
        } catch (SchedulerException e) {
            log.error(e.getMessage());
        }

        return checkExists;
    }

    /**
     * 新增定时任务
     */
    @Override
    public String addTask(PageTask pageTask) {
        //设置触发器信息，默认和job的key和group一致
        String[] jobKeyArr = pageTask.getJobKey().split("\\.");
        String jobName = jobKeyArr[0];
        String groupName = jobKeyArr[1];
        TriggerKey triggerKey = new TriggerKey(jobName, groupName);
        JobKey jobKey = new JobKey(pageTask.getJobKey(), groupName);
        try {
            Trigger trigger = scheduler.getTrigger(triggerKey);

            JobDataMap jobDataMap = new JobDataMap();
            Param[] mapData = pageTask.getMapData();
            if (mapData != null) {
                for (Param param :
                        mapData) {
                    jobDataMap.put(param.getKey(), param.getValue());
                }
            }


            SimpleScheduleBuilder simpleScheduleBuilder = null;
            if (trigger == null) {
                TriggerBuilder<Trigger> triggerTriggerBuilder = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withDescription(pageTask.getTriggerDescription());
                //如果是周期任务
                if (pageTask.getTaskType() == Constants.REPEAT_TASK) {
                    if (StringUtils.hasText(pageTask.getCron())) {
                        triggerTriggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(pageTask.getCron()));
                    }
                } else {//如果是非周期任务

                    /*设置重复执行的次数，重复次数不包括第一次执行的次数*/

                    // 如果任务的执行次数大于1，则需要设置间隔时间
                    if (pageTask.getRepeatCount() > 0) {
                        /*前台把时间全部转化成秒*/
                        simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withRepeatCount(pageTask.getRepeatCount()).withIntervalInSeconds(pageTask.getIntervalTime());
                    } else {
                        simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule().withRepeatCount(pageTask.getRepeatCount());
                    }
                    triggerTriggerBuilder.withSchedule(simpleScheduleBuilder);
                }

                // 设置任务的开始时间和结束时间
                if (pageTask.getStartDate() != null && pageTask.getEndDate() != null) {
                    trigger = triggerTriggerBuilder.startAt(pageTask.getStartDate()).endAt(pageTask.getEndDate())
                            .build();
                } else if (pageTask.getStartDate() != null) {
                    trigger = triggerTriggerBuilder.startAt(pageTask.getStartDate()).build();

                } else {
                    trigger = triggerTriggerBuilder.build();
                }
                /*设置作业*/
                JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) Class.forName(pageTask.getJobClass()))
                        .withIdentity(jobKey)
                        .setJobData(jobDataMap)
                        .withDescription(pageTask.getJobDescription())
                        .build();
                scheduler.scheduleJob(jobDetail, trigger);
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
        try {
            scheduler.pauseAll();
        } catch (SchedulerException e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 启动所有任务
     */
    @Override
    public boolean startAllTask(PageTask pageTask) {
        try {
            scheduler.resumeAll();
        } catch (SchedulerException e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean stopTask(PageTask pageTask) {

        if (!StrUtil.isBlankIfStr(pageTask.getJobName())) {
            String jobName = pageTask.getJobName();
            String[] jobNameArr = jobName.split("\\.");
            JobKey jobKey = new JobKey(jobName, jobNameArr[1]);
            try {
                scheduler.pauseJob(jobKey);
                return true;
            } catch (SchedulerException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean startTask(PageTask pageTask) {
        if (!StrUtil.isBlankIfStr(pageTask.getJobName())) {
            String jobName = pageTask.getJobName();
            String[] jobNameArr = jobName.split("\\.");
            JobKey jobKey = new JobKey(jobName, jobNameArr[1]);
            try {
                scheduler.resumeJob(jobKey);
                return true;
            } catch (SchedulerException e) {
                return false;
            }
        }
        return false;
    }
}
