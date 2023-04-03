package org.bsm.controller;


import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.annotation.RefreshSession;
import org.bsm.annotation.StatisticsQPS;
import org.bsm.pagemodel.PageFlow;
import org.bsm.service.IFlowableService;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.repository.Deployment;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
        Deployment deployment = processEngine.getRepositoryService().createDeployment().addString(pageFlow.getFlowName() + ".bpmn20.xml", pageFlow.getXml()).deploy();
        log.info("部署流程接口结束,{}", deployment);
        return Response.makeOKRsp(deployment != null);
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("查询流程列表接口")
    @PostMapping("/flowableList")
    public ResponseResult<JSONObject> flowableList(@RequestBody PageFlow pageFlow) {
        log.info("查询流程任务列表接口");
        JSONObject jsonObject = flowableService.getFlowList(pageFlow);
        log.info("查询流程任务列表接口结束");
        return Response.makeOKRsp(jsonObject);
    }

}

