package org.bsm.controller;


import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.service.ISystemDetailInfoService;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
@Api(tags = "首页控制类")
@Slf4j
@RestController
@RequestMapping("/system")
public class HomeController {


    @Autowired
    private ISystemDetailInfoService systemDetailInfoService;


    @ApiOperation("获取系统详细信息接口")
    @GetMapping("/getSystemDetailInfo")
    public ResponseResult<Object> getSystemDetailInfo() {
        log.info("获取系统详细信息接口");
        JSONObject warnMessageList = systemDetailInfoService.getSystemDetailInfo();
        return Response.makeOKRsp("获取系统详细信息接口成功").setData(warnMessageList);
    }

}

