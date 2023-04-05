package org.bsm.controller;


import com.alibaba.fastjson.JSON;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
    @ApiOperation("获取流程图接口")
    @GetMapping("/getFlowImg/{id}")
    public ResponseResult<Object> getFlowImg(@PathVariable("id") String id, HttpServletResponse response) {
        log.info("获取流程图接口");
        if (!StringUtils.hasText(id)) {
            return Response.makeErrRsp("参数错误");
        }
        try {
            flowableService.getFlowImg(id, response);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        log.info("获取流程图接口结束");
        return Response.makeOKRsp(true);
    }

}

