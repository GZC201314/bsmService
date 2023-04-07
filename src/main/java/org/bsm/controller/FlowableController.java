package org.bsm.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.annotation.RefreshSession;
import org.bsm.annotation.StatisticsQPS;
import org.bsm.entity.CurUser;
import org.bsm.pagemodel.PageFlow;
import org.bsm.service.IFlowableService;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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


    @RefreshSession
    @StatisticsQPS
    @ApiOperation("部署流程接口")
    @PostMapping("/deploy")
    public ResponseResult<Boolean> getTaskInfo(@RequestBody PageFlow pageFlow) {
        log.info("部署流程接口");
        if (Objects.isNull(pageFlow)) {
            return Response.makeErrRsp("参数错误");
        }
        boolean result = flowableService.deployFlow(pageFlow);
        log.info("部署流程接口结束,结果为{}", result);
        return Response.makeOKRsp(result);
    }
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("流程实例化接口")
    @PostMapping("/processInstance")
    public ResponseResult<JSONObject> processInstance(@RequestBody PageFlow pageFlow) {
        log.info("流程实例化接口");
        if (Objects.isNull(pageFlow)) {
            return Response.makeErrRsp("参数错误");
        }
        JSONObject jsonObject = flowableService.processInstance(pageFlow);
        log.info("流程实例化接口结束,结果为{}", jsonObject);
        return Response.makeOKRsp(jsonObject);
    }
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("获取流程Form定义接口")
    @PostMapping("/getFlowFormByFlowId")
    public ResponseResult<JSONObject> getFlowFormByFlowId(@RequestBody PageFlow pageFlow) {
        log.info("获取流程Form定义接口");
        if (Objects.isNull(pageFlow)) {
            return Response.makeErrRsp("参数错误");
        }
        JSONObject jsonObject = flowableService.getFlowFormByFlowId(pageFlow);
        log.info("获取流程Form定义接口结束,结果为{}", jsonObject);
        return Response.makeOKRsp(jsonObject);
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("任务详情接口")
    @PostMapping("/taskDetail")
    public ResponseResult<JSONObject> taskDetail(@RequestBody PageFlow pageFlow) {
        log.info("流程实例化接口");
        if (Objects.isNull(pageFlow)) {
            return Response.makeErrRsp("参数错误");
        }
        JSONObject jsonObject = flowableService.taskDetail(pageFlow);
        log.info("流程实例化接口结束,结果为{}", jsonObject);
        return Response.makeOKRsp(jsonObject);
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("查询流程列表接口")
    @PostMapping("/flowableList")
    public ResponseResult<JSONObject> flowableList(@RequestBody PageFlow pageFlow) {
        log.info("查询流程任务列表接口");
        if (Objects.isNull(pageFlow)) {
            return Response.makeErrRsp("参数错误");
        }
        JSONObject jsonObject = flowableService.getFlowList(pageFlow);
        log.info("查询流程任务列表接口结束");
        return Response.makeOKRsp(JSON.parseObject(JSON.toJSONStringWithDateFormat(jsonObject, "yyyy-MM-dd HH:mm:ss")));
    }
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("查询所有流程接口")
    @PostMapping("/allFlow")
    public ResponseResult<JSONArray> getAllFlow(@RequestBody PageFlow pageFlow) {
        log.info("查询所有流程接口");
        if (Objects.isNull(pageFlow)) {
            return Response.makeErrRsp("参数错误");
        }
        List<JSONObject> allFlow = flowableService.getAllFlow(pageFlow);
        log.info("查询所有流程接口结束");
        return Response.makeOKRsp(JSON.parseArray(JSON.toJSONStringWithDateFormat(allFlow, "yyyy-MM-dd HH:mm:ss")));
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("删除流程列表接口")
    @PostMapping("/deleteFlows")
    public ResponseResult<Boolean> deleteFlows(@RequestBody PageFlow pageFlow) {
        log.info("删除流程列表接口");
        if (Objects.isNull(pageFlow) || !StringUtils.hasText(pageFlow.getDelIds())) {
            return Response.makeErrRsp("参数错误");
        }
        Boolean result = flowableService.deleteFlows(pageFlow);
        log.info("删除流程列表接口结束");
        return Response.makeOKRsp(result);
    }
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("我的申请列表接口")
    @PostMapping("/myapplicationList")
    public ResponseResult<JSONObject> myapplicationList(@RequestBody PageFlow pageFlow, @RequestAttribute("curUser") CurUser curUser) {
        log.info("我的申请列表接口");
        if (Objects.isNull(pageFlow) || Objects.isNull(curUser)) {
            return Response.makeErrRsp("参数错误");
        }
        JSONObject myApplicationList = flowableService.getMyApplicationList(pageFlow, curUser);
        log.info("我的申请列表接口结束");
        return Response.makeOKRsp(myApplicationList);
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("获取流程图接口")
    @GetMapping("/getFlowImg/{id}")
    public void getFlowImg(@PathVariable("id") String id, HttpServletResponse response) {
        log.info("获取流程图接口");
        if (!StringUtils.hasText(id)) {
            return;
        }
        try {
            flowableService.getFlowImg(id, response);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        log.info("获取流程图接口结束");
    }
    @RefreshSession
    @StatisticsQPS
    @ApiOperation("获取申请流程图接口")
    @GetMapping("/getTaskFlowDiagram/{id}")
    public void getTaskFlowDiagram(@PathVariable("id") String id, HttpServletResponse response) {
        log.info("获取申请流程图接口");
        if (!StringUtils.hasText(id)) {
            return;
        }
        try {
            flowableService.getTaskFlowDiagram(id, response);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        log.info("获取申请流程图接口结束");
    }

}

