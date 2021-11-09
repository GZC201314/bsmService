package org.bsm.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.pagemodel.PageUpload;
import org.bsm.service.impl.AIServiceImpl;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author GZC
 * @create 2021-11-09 21:01
 * @desc 百度智能控制类
 */
@Api(tags = "百度智能控制类")
@Slf4j
@RestController
@RequestMapping("/ai")
public class AIController {
    /**
     * 上传文字识别图像
     */
    @Autowired
    AIServiceImpl aiService;

    @ApiOperation("图像文字识别接口")
    @PostMapping(value = "ocr", consumes = "multipart/*", headers = "content-type=multipart/form-data")
    public ResponseResult<Object> orc(PageUpload pageUpload) {
        log.info("into the ocr function");
        try {
            String result = aiService.ocr(pageUpload);
            return Response.makeOKRsp("图片识别成功").setData(result);
        } catch (Exception e) {
            log.error(e.getMessage());
            return Response.makeErrRsp("图片识别失败");
        }
    }
}
