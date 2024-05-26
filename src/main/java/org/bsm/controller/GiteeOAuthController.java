package org.bsm.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.annotation.RefreshSession;
import org.bsm.annotation.StatisticsQPS;
import org.bsm.service.IGiteeService;
import org.bsm.service.IRoleService;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 角色控制器
 * </p>
 *
 * @author GZC
 * @since 2021-10-29
 */
@Api(tags = "Gitee Oauth 控制接口类")
@RestController
@Slf4j
@RequestMapping("/gitee")
public class GiteeOAuthController {

    @Autowired
    IGiteeService giteeService;

    @StatisticsQPS
    @ApiOperation("获取所有用户角色接口")
    @GetMapping("/oAuthLogin")
    public ResponseResult<Object> getAllRole(String code) {
        log.info("获取gitee oauth,授权码是 :{}" , code);
        giteeService.oAuthLogin(code);
        return Response.makeOKRsp("获取所有的用户角色成功").setData(null);
    }
}

