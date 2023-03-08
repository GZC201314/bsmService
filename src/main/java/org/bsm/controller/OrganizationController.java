package org.bsm.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.annotation.RefreshSession;
import org.bsm.annotation.StatisticsQPS;
import org.bsm.entity.CurUser;
import org.bsm.entity.Organization;
import org.bsm.pagemodel.PageOrganization;
import org.bsm.pagemodel.PageRole;
import org.bsm.service.IOrganizationService;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author GZC
 * @since 2023-03-06
 */
@RestController
@RequestMapping("/organization")
@Api(tags = "组织控制接口类")
@Slf4j
public class OrganizationController {

    @Resource
    IOrganizationService organizationService;

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("获取组织分页接口")
    @PostMapping("/getPage")
    public ResponseResult<Object> getPage(@RequestBody PageOrganization pageOrganization) {
        log.info("获取组织分页,使用的查询条件是 :" + pageOrganization);
        Organization organization = new Organization();
        BeanUtils.copyProperties(pageOrganization, organization);
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pageOrganization.getPage().getSearch())) {
            queryWrapper.like("name", pageOrganization.getPage().getSearch());
        }
        Page<Organization> page = new Page<>(pageOrganization.getPage().getPage(), pageOrganization.getPage().getPageSize());
        Page<Organization> organizationPage = organizationService.page(page, queryWrapper);
        return Response.makeOKRsp("获取组织分页").setData(organizationPage);
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("获取组织详细信息接口")
    @PostMapping("/getDetailInfo")
    public ResponseResult<Object> getDetailInfo(@RequestBody PageOrganization pageOrganization) {
        log.info("获取组织详细信息,使用的查询条件是 :" + pageOrganization);
        Organization organization = new Organization();
        BeanUtils.copyProperties(pageOrganization, organization);
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        if (pageOrganization.getId() != null) {
            queryWrapper.eq("id", pageOrganization.getId());
        }
        if (StringUtils.hasText(pageOrganization.getName())) {
            queryWrapper.like("name", pageOrganization.getName());
        }
        organization = organizationService.getOne(queryWrapper);
        return Response.makeOKRsp("获取用户角色详细信息成功").setData(organization);
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("校验组织名接口")
    @PostMapping("/validateName")
    public ResponseResult<Object> validateName(@RequestBody PageOrganization pageOrganization) {
        log.info("获取组织详细信息,使用的查询条件是 :" + pageOrganization);
        Organization organization = new Organization();
        BeanUtils.copyProperties(pageOrganization, organization);
        QueryWrapper<Organization> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pageOrganization.getName())) {
            queryWrapper.eq("name", pageOrganization.getName());
        }
        organization = organizationService.getOne(queryWrapper);
        return Response.makeOKRsp("获取用户角色详细信息成功").setData(Objects.isNull(organization));
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("新增组织接口")
    @PostMapping(value = "/add", consumes = "multipart/*", headers = "content-type=multipart/form-data")
    public ResponseResult<Object> add( PageOrganization pageOrganization,@RequestAttribute("AuthorityParam") CurUser curUser) {
        log.info("新增组织接口");
        boolean result = organizationService.addOrganization(pageOrganization,curUser);
        if (result) {
            return Response.makeOKRsp("新增组织成功");
        } else {
            return Response.makeOKRsp("新增组织失败");
        }
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("修改组织接口")
    @PostMapping(value = "/update", consumes = "multipart/*", headers = "content-type=multipart/form-data")
    public ResponseResult<Object> update(PageOrganization organization) {
        log.info("修改组织接口");
        boolean result = organizationService.updateOrganization(organization);
        if (result) {
            return Response.makeOKRsp("修改组织成功");
        } else {
            return Response.makeOKRsp("修改组织失败");
        }
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("获取所有组织接口")
    @GetMapping("/list")
    public ResponseResult<List<Organization>> list() {
        log.info("获取所有组织接口");
        List<Organization> list = organizationService.list();
        return new ResponseResult<List<Organization>>().setData(list).setCode(200).setMsg("获取所有组织成功");
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("删除组织接口")
    @PostMapping("/delete")
    public ResponseResult<Object> deleteRoles(@RequestBody PageOrganization organization) {
        log.info("删除组织接口");
        String delIds = organization.getDelIds();
        if (!StringUtils.hasText(delIds)) {
            return Response.makeErrRsp("参数错误");
        }
        List<String> idList = Arrays.asList(delIds.split(","));
        int[] idArr = idList.stream().mapToInt(Integer::parseInt).toArray();
        boolean result = organizationService.removeByIds(Collections.singletonList(idArr));
        if (result) {
            return Response.makeOKRsp("删除组织成功");
        } else {
            return Response.makeOKRsp("删除组织失败");
        }
    }

}
