package org.bsm.controller;


import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.annotation.RefreshSession;
import org.bsm.annotation.StatisticsQPS;
import org.bsm.pagemodel.PageObject;
import org.bsm.pagemodel.PageTask;
import org.bsm.service.ITaskService;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
@Api(tags = "定时任务控制类")
@Slf4j
@RestController
@RequestMapping("/task")
public class TaskController {


    @Autowired
    private ITaskService taskService;
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("获取定时任务分页列表接口")
    @PostMapping("/getTaskPageList")
    public ResponseResult<Object> getTaskPageList(@RequestBody PageTask pageTask) {
        log.info("获取定时任务分页列表接口");
        if (pageTask.getPage() == null) {
            return Response.makeErrRsp("获取定时任务分页列表接口失败，参数不正确。");
        }
        PageObject<JSONObject> allTaskPage = taskService.getAllTaskPage(pageTask);
        return Response.makeOKRsp("获取定时任务分页列表接口成功").setData(allTaskPage);
    }
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("获取定时任务详细信息接口")
    @GetMapping("/getTaskInfo")
    public ResponseResult<Object> getTaskInfo(PageTask pageTask) {
        log.info("获取定时任务详细信息接口");
        if (!StringUtils.hasText(pageTask.getJobKey())) {
            return Response.makeErrRsp("获取定时任务详细信息接口失败，参数不正确。");
        }
        JSONObject taskInfo = taskService.getTaskInfo(pageTask);
        return Response.makeOKRsp("获取定时任务详细信息接口成功").setData(taskInfo);
    }
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("新增定时任务接口")
    @PostMapping("/addTask")
    public ResponseResult<Object> addTask(@RequestBody PageTask pageTask) {
        log.info("新增定时任务接口");
        if (pageTask == null) {
            return Response.makeErrRsp("新增定时任务接口失败，参数不正确。");
        }
        String nextFireTime = taskService.addTask(pageTask);
        return Response.makeOKRsp("获取定时任务分页列表接口成功").setData(nextFireTime);
    }
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("校验定时任务Key接口")
    @GetMapping("/validateJobKey")
    public ResponseResult<Object> validateJobKey(PageTask pageTask) {
        log.info("校验定时任务Key接口");
        if (!StringUtils.hasText(pageTask.getJobKey())) {
            return Response.makeErrRsp("校验定时任务Key接口失败，参数不正确。");
        }
        boolean checkExists = taskService.validateJobKey(pageTask);
        return Response.makeOKRsp("校验定时任务Key接口成功").setData(!checkExists);
    }
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("删除定时任务接口")
    @GetMapping("/deleteTasks")
    public ResponseResult<Object> deleteTasks(PageTask pageTask) {
        log.info("删除定时任务接口");
        if (!StringUtils.hasText(pageTask.getDelIds())) {
            return Response.makeErrRsp("删除定时任务接口失败，参数不正确。");
        }
        boolean checkExists = taskService.deleteTasks(pageTask);
        return Response.makeOKRsp("删除定时任务接口成功").setData(checkExists);
    }
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("停止所有定时任务接口")
    @GetMapping("/stopAllTasks")
    public ResponseResult<Object> stopAllTasks(PageTask pageTask) {
        log.info("停止所有定时任务接口");
        boolean checkExists = taskService.stopAllTask(pageTask);
        return Response.makeOKRsp("停止所有定时任务接口成功").setData(checkExists);
    }
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("启动所有定时任务接口")
    @GetMapping("/startAllTasks")
    public ResponseResult<Object> startAllTasks(PageTask pageTask) {
        log.info("启动所有定时任务接口");
        boolean checkExists = taskService.startAllTask(pageTask);
        return Response.makeOKRsp("启动所有定时任务接口成功").setData(checkExists);
    }
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("停止单个定时任务接口")
    @GetMapping("/stopTask")
    public ResponseResult<Object> stopTask(PageTask pageTask) {
        log.info("停止单个定时任务接口");
        boolean checkExists = taskService.stopTask(pageTask);
        return Response.makeOKRsp("停止单个定时任务接口成功").setData(checkExists);
    }
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("启动单个定时任务接口")
    @GetMapping("/startTask")
    public ResponseResult<Object> startTask(PageTask pageTask) {
        log.info("启动单个定时任务接口");
        boolean checkExists = taskService.startTask(pageTask);
        return Response.makeOKRsp("启动单个定时任务接口成功").setData(checkExists);
    }
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("立即执行定时任务接口")
    @GetMapping("/executeNowTask")
    public ResponseResult<Object> executeNowTask(PageTask pageTask) {
        log.info("立即执行定时任务接口");
        boolean checkExists = taskService.executeNowTask(pageTask);
        return Response.makeOKRsp("立即执行定时任务接口成功").setData(checkExists);
    }

}

