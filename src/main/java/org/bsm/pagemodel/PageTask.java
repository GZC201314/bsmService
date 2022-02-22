package org.bsm.pagemodel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

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
     * 任务类型 0 cycleType,1 oneTimeTask
     */
    private int taskType;

    /**
     * 定时任务Key
     */
    private String jobKey;

    /**
     * 定时任务组
     */

    private String group;

    /**
     * 定时任务全类名
     */

    private String jobClass;
    /**
     * 定时任务描述
     */
    private String jobDescription;
    /**
     * 定时任务触发器描述
     */
    private String triggerDescription;
    /**
     * cron 表达式
     */
    private String cron;
    /**
     * 触发器组
     */
    private String triggerGroup;
    /**
     * 任务开始时间
     */

    /**
     * 任务重复次数
     */
    private int repeatCount;
    /**
     * 任务间隔时间
     */
    private int intervalTime;

    private Date startDate;
    /**
     * 任务开始时间
     */
    private Date endDate;

    /**
     * 触发器名字
     */
    private String triggerName;


    /**
     * 任务参数
     */
    private Param[] mapData;


    /**
     * 分页 参数
     */
    private Page page;


    private String delIds;
}
