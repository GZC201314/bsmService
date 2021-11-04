package org.bsm.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.Pages;
import org.bsm.pagemodel.PagePages;
import org.bsm.service.impl.PagesServiceImpl;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
@Api(tags = "页面控制类")
@RestController
@Slf4j
@RequestMapping("/pages")
public class PagesController {

    @Autowired
    PagesServiceImpl pageService;

    @ApiOperation("获取所有页面接口")
    @GetMapping("/getAllPages")
    public ResponseResult<Object> getAllPages(PagePages pagePages) {
        log.info("获取所有的页面,使用的查询条件是 :" + pagePages);
        Pages pages = new Pages();
        BeanUtils.copyProperties(pagePages, pages);
        QueryWrapper<Pages> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pagePages.getTitle())) {
            queryWrapper.like("title", pagePages.getTitle());
        }
        List<Pages> roleList = pageService.list(queryWrapper);
        return Response.makeOKRsp("获取所有的页面成功").setData(roleList);
    }

    @ApiOperation("获取所有页面接口(分页)")
    @GetMapping("/getPagePages")
    public ResponseResult<Object> getPagePages(PagePages pagePages) {
        log.info("获取所有页面接口(分页),使用的查询条件是 :" + pagePages);
        Pages pages = new Pages();
        BeanUtils.copyProperties(pagePages, pages);
        QueryWrapper<Pages> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pagePages.getTitle())) {
            queryWrapper.like("title", pagePages.getTitle());
        }
        Page<Pages> page = new Page<>(pagePages.getCurrent(), pagePages.getSize(), true);
        Page<Pages> rolePage = pageService.page(page, queryWrapper);
        return Response.makeOKRsp("获取所有页面接口(分页)成功").setData(rolePage);
    }

    @ApiOperation("获取页面详细信息接口")
    @GetMapping("/getPagesInfo")
    public ResponseResult<Object> getPagesInfo(PagePages pagePages) {
        log.info("获取页面详细信息接口,使用的查询条件是 :" + pagePages);
        Pages pages = new Pages();
        BeanUtils.copyProperties(pagePages, pages);
        QueryWrapper<Pages> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pagePages.getPageid())) {
            queryWrapper.eq("pageid", pagePages.getPageid());
        }
        pages = pageService.getOne(queryWrapper);
        return Response.makeOKRsp("获取页面详细信息成功").setData(pages);
    }

    @ApiOperation("新增页面接口")
    @PostMapping("/addPages")
    public ResponseResult<Object> addPages(Pages pages) {
        pages.setPageid(UUID.randomUUID().toString());
        log.info("新增页面,信息是 :" + pages);
        boolean result = pageService.save(pages);
        if (result) {
            return Response.makeOKRsp("新增页面成功");
        } else {
            return Response.makeErrRsp("新增页面失败");
        }
    }

    @ApiOperation("修改页面接口")
    @PostMapping("/updatePages")
    public ResponseResult<Object> updatePages(Pages pages) {
        log.info("修改页面,信息是 :" + pages);
        boolean result = pageService.saveOrUpdate(pages);
        if (result) {
            return Response.makeOKRsp("修改页面成功");
        } else {
            return Response.makeErrRsp("修改页面失败");
        }
    }

    @ApiOperation("删除页面接口")
    @DeleteMapping("/deletePages")
    public ResponseResult<Object> deletePages(PagePages pagePages) {
        log.info("删除页面,信息是 :" + pagePages);
        UpdateWrapper<Pages> deleteWrapper = new UpdateWrapper<>();
        if (StringUtils.hasText(pagePages.getPageid())) {
            deleteWrapper.eq("pageid", pagePages.getPageid());
        }

        boolean result = pageService.remove(deleteWrapper);
        if (result) {
            return Response.makeOKRsp("删除页面成功");
        } else {
            return Response.makeErrRsp("删除页面失败");
        }
    }
}

