package org.bsm.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.annotation.StatisticsQPS;
import org.bsm.utils.RedisUtil;
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
@Api(tags = "RSA公钥暴露控制类")
@Slf4j
@RestController
@RequestMapping("/publicKey")
public class RSAKeyPairController {


    @Autowired
    private RedisUtil redisUtil;

    @StatisticsQPS
    @ApiOperation("获取RSA公钥接口")
    @GetMapping("/get")
    public ResponseResult<Object> getPublicKey() {
        log.info("获取RSA公钥接口");
        String rsaPublicKey = (String) redisUtil.get("rsaPublicKey");
        return Response.makeOKRsp("获取RSA公钥接口成功").setData(rsaPublicKey);
    }

}

