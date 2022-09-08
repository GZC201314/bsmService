package org.bsm.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.annotation.RefreshSession;
import org.bsm.annotation.StatisticsQPS;
import org.bsm.entity.Datasource;
import org.bsm.pagemodel.PageDataSource;
import org.bsm.pagemodel.PageUpload;
import org.bsm.service.IDatasourceService;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-01-06
 */
@Api(tags = "数据源配置控制类")
@Slf4j
@RestController
@RequestMapping("/datasource")
public class DatasourceController {

    @Autowired
    IDatasourceService datasourceService;

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("获取所有用户角色接口(分页)")
    @PostMapping("/getDataSourceList")
    public ResponseResult<Object> getDataSourceList(@RequestBody PageDataSource pageDataSource) {
        log.info("获取所有的用户角色(分页),使用的查询条件是 :" + pageDataSource);
        QueryWrapper<Datasource> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pageDataSource.getPage().getSearch())) {
            queryWrapper.like("sourcename", pageDataSource.getPage().getSearch());
        }

        if (pageDataSource.getPage() == null) {
            return Response.makeErrRsp("参数错误");
        }
        Page<Datasource> page = new Page<>(pageDataSource.getPage().getPage(), pageDataSource.getPage().getPageSize());
        Page<Datasource> datasourcePage = datasourceService.page(page, queryWrapper);

        return Response.makeOKRsp("获取所有的用户角色(分页)成功").setData(datasourcePage);
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("数据源驱动上传")
    @PostMapping(value = "uploadDrive", consumes = "multipart/*", headers = "content-type=multipart/form-data")
    public ResponseResult<Object> uploadDrive(PageUpload pageUpload) {
        log.info("数据源驱动上传");
        if (pageUpload == null || pageUpload.getFile() == null) {
            return Response.makeErrRsp("数据源驱动上传失败，原因是参数错误");
        }
        try {

            String newAvatarUrl = datasourceService.uploadDrive(pageUpload);
            if (StringUtils.hasText(newAvatarUrl)) {
                return Response.makeOKRsp("数据源驱动上传成功").setData(newAvatarUrl);
            } else {
                return Response.makeErrRsp("数据源驱动上传失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return Response.makeErrRsp("数据源驱动上传失败");
        }
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("测试数据源配置")
    @PostMapping("/testDataSource")
    public ResponseResult<Object> testDataSource(@RequestBody PageDataSource pageDataSource) {
        log.info("测试数据源配置,使用的查询条件是 :" + pageDataSource);
        return Response.makeOKRsp("测试数据源配置成功").setData(datasourceService.testDrive(pageDataSource));
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("新增数据源配置接口")
    @PostMapping("/insertDataSource")
    public ResponseResult<Object> insertDataSource(@RequestBody PageDataSource pageDataSource) {
        log.info("新增数据源配置接口,信息是 :" + pageDataSource);
        if (pageDataSource == null) {
            return Response.makeErrRsp("新增数据源配置接口错误，参数错误。");
        }
        Datasource datasource = new Datasource();
        BeanUtils.copyProperties(pageDataSource, datasource);
        boolean result = datasourceService.save(datasource);
        if (result) {
            return Response.makeOKRsp("新增数据源配置接口成功");
        } else {
            return Response.makeErrRsp("新增数据源配置接口失败");
        }
    }

    @StatisticsQPS
    @ApiOperation("删除数据源配置接口，支持批量删除")
    @PostMapping("/deleteDataSource")
    public ResponseResult<Object> deleteRoles(@RequestBody PageDataSource pageDataSource) {
        log.info("删除数据源配置接口,信息是 :" + pageDataSource);
        if (!StringUtils.hasText(pageDataSource.getDelIds())) {
            return Response.makeErrRsp("参数错误");
        }

        String[] delIds = pageDataSource.getDelIds().split(",");

        boolean result = datasourceService.removeByIds(Arrays.asList(delIds));

        if (result) {
            return Response.makeOKRsp("删除数据源配置接口成功");
        } else {
            return Response.makeErrRsp("删除数据源配置接口失败");
        }
    }


}

