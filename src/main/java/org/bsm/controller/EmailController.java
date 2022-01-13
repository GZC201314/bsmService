package org.bsm.controller;


import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.pagemodel.PageMessage;
import org.bsm.service.IWarnMessageService;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@Api(tags = "警告消息管理控制类")
@Slf4j
@RestController
@RequestMapping("/warnMessage")
public class EmailController {


    @Autowired
    private IWarnMessageService warnMessageService;


    @ApiOperation("获取告警邮件列表接口")
    @PostMapping("/getWarnMessageList")
    public ResponseResult<Object> getWarnMessageList(@RequestBody PageMessage pageMessage) {
        log.info("获取告警邮件列表接口,信息是 :" + pageMessage);
        if (pageMessage.getPage() == null) {
            return Response.makeErrRsp("参数错误，请输入分页信息。");
        }
        JSONObject warnMessageList = warnMessageService.getWarnMessageList(pageMessage);
        return Response.makeOKRsp("获取告警邮件列表接口成功").setData(warnMessageList);
    }

}

