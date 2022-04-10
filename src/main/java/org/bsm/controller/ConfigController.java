package org.bsm.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.Config;
import org.bsm.pagemodel.PageConfig;
import org.bsm.service.IConfigService;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2021-11-09
 */
@RestController
@RequestMapping("/config")
@Slf4j
@Api(tags = "配置信息控制类")
public class ConfigController {
    @Resource
    IConfigService configService;

    @ApiOperation("获取所有配置信息接口(分页)")
    @PostMapping("/getConfigList")
    public ResponseResult<Object> getDataSourceList(@RequestBody PageConfig pageConfig) {
        log.info("获取所有配置信息接口(分页),使用的查询条件是 :" + pageConfig);
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pageConfig.getPage().getSearch())) {
            queryWrapper.like("remark", pageConfig.getPage().getSearch());
        }

        if (pageConfig.getPage() == null) {
            return Response.makeErrRsp("参数错误");
        }
        Page<Config> page = new Page<>(pageConfig.getPage().getPage(), pageConfig.getPage().getPageSize());
        Page<Config> datasourcePage = configService.page(page, queryWrapper);

        return Response.makeOKRsp("获取所有配置信息接口(分页)成功").setData(datasourcePage);
    }

    @ApiOperation("获取所有配置信息接口")
    @GetMapping("/getAllConfig")
    public ResponseResult<Object> getAllConfig(PageConfig pageConfig) {
        log.info("获取所有配置信息接口,使用的查询条件是 :" + pageConfig);
        Config config = new Config();
        BeanUtils.copyProperties(pageConfig, config);
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pageConfig.getRemark())) {
            queryWrapper.like("remark", pageConfig.getRemark());
        }
        List<Config> configList = configService.list(queryWrapper);
        return Response.makeOKRsp("获取所有配置信息接口成功").setData(configList);
    }


    @ApiOperation("获取配置信息详细信息接口")
    @GetMapping("/getConfigInfo")
    public ResponseResult<Object> getConfigInfo(PageConfig pageConfig) {
        log.info("获取配置信息详细信息,使用的查询条件是 :" + pageConfig);
        Config config = new Config();
        BeanUtils.copyProperties(pageConfig, config);
        QueryWrapper<Config> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isBlank(pageConfig.getName())) {
            queryWrapper.eq("name", pageConfig.getName());
        }
        config = configService.getOne(queryWrapper);
        return Response.makeOKRsp("获取配置信息详细信息接口成功").setData(config);
    }

    @ApiOperation("新增配置信息接口")
    @PostMapping("/addConfig")
    public ResponseResult<Object> addConfig(@RequestBody Config config) {
        log.info("新增配置信息接口,配置信息是 : {}", config);
        boolean result = configService.addConfig(config);
        if (result) {
            return Response.makeOKRsp("新增配置信息成功");
        } else {
            return Response.makeOKRsp("新增配置信息失败");
        }
    }

    @ApiOperation("修改配置信息接口")
    @PostMapping("/updateConfig")
    public ResponseResult<Object> updateConfig(@RequestBody Config config) {
        log.info("修改配置信息,配置信息信息是 : {}", config);
        boolean result = configService.editConfig(config);
        if (result) {
            return Response.makeOKRsp("修改配置信息接口成功");
        } else {
            return Response.makeOKRsp("修改配置信息接口失败");
        }
    }

    @ApiOperation("删除配置信息接口")
    @GetMapping("/deleteConfigs")
    public ResponseResult<Object> deleteRoles(String delIds) {
        log.info("删除配置信息接口,配置信息的主键是 : {}", delIds);
        if (!StringUtils.hasText(delIds)) {
            return Response.makeErrRsp("参数错误");
        }
        boolean result = configService.delConfigs(delIds);
        if (result) {
            return Response.makeOKRsp("删除配置信息成功");
        } else {
            return Response.makeOKRsp("删除配置信息失败");
        }
    }

}

