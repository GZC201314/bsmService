package org.bsm.controller;


import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.annotation.RefreshSession;
import org.bsm.annotation.StatisticsQPS;
import org.bsm.entity.CurUser;
import org.bsm.pagemodel.PageFlow;
import org.bsm.pagemodel.PageObject;
import org.bsm.pagemodel.PageTask;
import org.bsm.service.IFlowableService;
import org.bsm.service.ITaskService;
import org.bsm.service.impl.FlowableServiceImpl;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.flowable.common.spring.AutoDeploymentStrategy;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.impl.ProcessDefinitionQueryImpl;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.spring.configurator.SpringProcessEngineConfigurator;
import org.flowable.spring.configurator.DefaultAutoDeploymentStrategy;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
@Api(tags = "流程控制类")
@Slf4j
@RestController
@RequestMapping("/flowable")
public class FlowableController {

    @Resource
    private IFlowableService flowableService;


    @Resource
    private ProcessEngine processEngine;
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("部署流程接口")
    @PostMapping("/deploy")
    public ResponseResult<Boolean> getTaskInfo(@RequestBody PageFlow pageFlow) {
        log.info("部署流程接口");
        Deployment deployment = processEngine.getRepositoryService().createDeployment().addString(pageFlow.getFlowName()+".bpmn20.xml", pageFlow.getXml()).deploy();
        log.info("部署流程接口结束,{}",deployment);
        return Response.makeOKRsp(deployment!=null);
    }
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("查询流程列表接口")
    @GetMapping("/flowableList")
    public ResponseResult<List<JSONObject>> userTaskList(@RequestAttribute(value = "curUser",required = false) CurUser curUser) {
        log.info("查询流程任务列表接口");
        List<JSONObject> list = flowableService.getFlowList();
        log.info("查询流程任务列表接口结束");
        return Response.makeOKRsp(list);
    }

}

