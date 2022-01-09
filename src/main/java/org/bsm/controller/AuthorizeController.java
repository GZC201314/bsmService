package org.bsm.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.Pages;
import org.bsm.pagemodel.PagePages;
import org.bsm.pagemodel.PageRole;
import org.bsm.service.IAuthorizeService;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
@Api(tags = "权限管理控制类")
@Slf4j
@RestController
@RequestMapping("/authorize")
public class AuthorizeController {


    @Autowired
    private IAuthorizeService authorizeService;

    @ApiOperation("获取角色所有授权页面接口")
    @GetMapping("/getAllAuthorizePagesByRoleName")
    public ResponseResult<Object> getAllAuthorizePagesByRoleName(PageRole pageRole) {
        log.info("获取角色所有授权页面,使用的查询条件是:" + pageRole);

        if (!StringUtils.hasText(pageRole.getRolename())) {
            Response.makeErrRsp("参数错误");
        }

        JSONObject allAuthorizePagesByRoleName = authorizeService.getAllAuthorizePagesByRoleName(pageRole);
        return Response.makeOKRsp("获取所有的用户角色成功").setData(allAuthorizePagesByRoleName.toJSONString());
    }


    @ApiOperation("修改角色授权页面接口")
    @PostMapping("/updateAuthorizePagesByRoleName")
    public ResponseResult<Object> updateAuthorizePagesByRoleName(@RequestBody PagePages pages) {
        log.info("修改角色授权页面接口,信息是 :" + pages);
        Pages page = new Pages();
        BeanUtils.copyProperties(pages, page);
        UpdateWrapper<Pages> pagesUpdateWrapper = new UpdateWrapper<>();
        if (!StringUtils.hasText(pages.getPagesIds()) || !StringUtils.hasText(pages.getRolename())) {
            return Response.makeErrRsp("修改角色授权页面接口参数错误。");
        }

        boolean result = authorizeService.updateAuthorizePagesByRoleName(pages);
        if (result) {
            return Response.makeOKRsp("修改页面成功");
        } else {
            return Response.makeErrRsp("修改页面失败");
        }
    }

}

