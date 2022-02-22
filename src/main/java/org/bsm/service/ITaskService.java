package org.bsm.service;

import com.alibaba.fastjson.JSONObject;
import org.bsm.pagemodel.PageObject;
import org.bsm.pagemodel.PageTask;

public interface ITaskService {

    /**
     * 获取分页的定时任务信息
     */
    public PageObject<JSONObject> getAllTaskPage(PageTask pageTask);

    /**
     * 删除定时任务，支持批量删除
     */
    public boolean deleteTasks(PageTask pageTask);


    /**
     * 获取定时任务的详细信息
     */
    public JSONObject getTaskInfo(PageTask pageTask);

    /**
     * 校验定时任务Key
     */
    public boolean validateJobKey(PageTask pageTask);

    /**
     * 新增定时任务
     */
    public String addTask(PageTask pageTask);

    /**
     * 关闭所有任务
     */
    public boolean stopAllTask(PageTask pageTask);

    /**
     * 启动所有任务
     */
    public boolean startAllTask(PageTask pageUpload);
}
