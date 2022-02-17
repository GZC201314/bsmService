package org.bsm.pagemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author GZC
 * @create 2021-11-05 15:38
 * @desc 定时任务实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageTask {
    /**
     * 定时任务Key
     */
    private String jobKey;

    /**
     * 定时任务组
     */

    private String group;
    /**
     * 定时任务描述
     */
    private String jobDescription;
    /**
     * cron 表达式
     */
    private String cron;
    /**
     * 触发器组
     */
    private String triggerGroup;
    /**
     * 触发器名字
     */
    private String triggerName;


    /**
     * 任务参数
     */
    private Map<String, Object> jobDataMap;


    /**
     * 分页 参数
     */
    private Page page;


    private String delIds;
}
