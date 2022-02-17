package org.bsm.controller;


import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.pagemodel.PageObject;
import org.bsm.pagemodel.PageTask;
import org.bsm.service.ITaskService;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
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


    @ApiOperation("获取定时任务分页列表接口")
    @PostMapping("/getTaskPageList")
    public ResponseResult<Object> getTaskPageList(@RequestBody PageTask pageTask) {
        log.info("获取定时任务分页列表接口");
        PageObject<JSONObject> allTaskPage = taskService.getAllTaskPage(pageTask);
        return Response.makeOKRsp("获取定时任务分页列表接口成功").setData(allTaskPage);
    }

    @ApiOperation("获取定时任务详细信息接口")
    @GetMapping("/getTaskInfo")
    public ResponseResult<Object> getTaskInfo(PageTask pageTask) {
        log.info("获取定时任务详细信息接口");
        JSONObject taskInfo = taskService.getTaskInfo(pageTask);
        return Response.makeOKRsp("获取定时任务详细信息接口成功").setData(taskInfo);
    }

}

