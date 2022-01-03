package org.bsm.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.Pages;
import org.bsm.pagemodel.PagePages;
import org.bsm.service.IPagesService;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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
    IPagesService pageService;

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
    @PostMapping("/getPagePages")
    public ResponseResult<Object> getPagePages(@RequestBody PagePages pagePages) {
        log.info("获取所有页面接口(分页),使用的查询条件是 :" + pagePages);
        Pages pages = new Pages();
        BeanUtils.copyProperties(pagePages, pages);
        QueryWrapper<Pages> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pagePages.getPage().getSearch())) {
            queryWrapper.like("title", pagePages.getPage().getSearch());
        }
        Page<Pages> page = new Page<>(pagePages.getPage().getPage(), pagePages.getPage().getPageSize(), true);
        Page<Pages> pagesPage = pageService.page(page, queryWrapper);
        return Response.makeOKRsp("获取所有页面接口(分页)成功").setData(pagesPage);
    }

    @ApiOperation("根据pageid获取页面详细信息接口")
    @GetMapping("/getPagesInfo")
    public ResponseResult<Object> getPagesInfo(PagePages pagePages) {
        log.info("根据pageid获取页面详细信息接口,使用的查询条件是 :" + pagePages);
        QueryWrapper<Pages> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pagePages.getPageid())) {
            queryWrapper.eq("pageid", pagePages.getPageid());
        } else {
            Response.makeErrRsp("根据pageid获取页面详细信息失败，参数错误！");
        }
        Pages pages = pageService.getOne(queryWrapper);
        return Response.makeOKRsp("根据pageid获取页面详细信息成功").setData(pages);
    }

    @ApiOperation("校验pageKey的唯一性接口")
    @GetMapping("/pageKeyUnique")
    public ResponseResult<Object> pageKeyUnique(PagePages pagePages) {
        log.info("校验pageKey的唯一性接口,使用的查询条件是 :" + pagePages);
        QueryWrapper<Pages> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pagePages.getPagekey())) {
            queryWrapper.eq("pagekey", pagePages.getPagekey());
        } else {
            Response.makeErrRsp("校验pageKey的唯一性接口失败，参数错误！");
        }
        int count = pageService.count(queryWrapper);
        return Response.makeOKRsp("校验pageKey的唯一性成功").setData(count == 0);
    }

    @ApiOperation("获取所有的父节点接口")
    @GetMapping("/getparentNode")
    public ResponseResult<Object> getparentNode() {
        log.info("获取所有的父节点接口");
        QueryWrapper<Pages> pagesQueryWrapper = new QueryWrapper<>();
        pagesQueryWrapper.eq("parentkey", "0");
        List<Pages> list = pageService.list(pagesQueryWrapper);
        return Response.makeOKRsp("所有的父节点信息成功").setData(list);
    }

    @ApiOperation("新增页面接口")
    @PostMapping("/addPages")
    public ResponseResult<Object> addPages(@RequestBody PagePages pages) {
        pages.setPageid(UUID.randomUUID().toString());
        log.info("新增页面,信息是 :" + pages);
        boolean result = pageService.addPages(pages);
        if (result) {
            return Response.makeOKRsp("新增页面成功");
        } else {
            return Response.makeErrRsp("新增页面失败");
        }
    }

    @ApiOperation("修改页面接口")
    @PostMapping("/updatePages")
    public ResponseResult<Object> updatePages(@RequestBody PagePages pages) {
        log.info("修改页面,信息是 :" + pages);
        Pages page = new Pages();
        BeanUtils.copyProperties(pages, page);
        UpdateWrapper<Pages> pagesUpdateWrapper = new UpdateWrapper<>();
        if (!StringUtils.hasText(page.getPageid())) {
            return Response.makeErrRsp("修改页面参数错误。");
        }
        pagesUpdateWrapper.eq("pageid", page.getPageid());
        pagesUpdateWrapper.set("icon", page.getIcon());
        pagesUpdateWrapper.set("pagepath", page.getPagepath());
        pagesUpdateWrapper.set("title", page.getTitle());
        pagesUpdateWrapper.set("parentkey", page.getParentkey());
        if ("0".equals(page.getParentkey())) {
            pagesUpdateWrapper.set("icontype", "fa");

        } else {
            pagesUpdateWrapper.set("icontype", "antd");
            pagesUpdateWrapper.set("icon", "");
        }
        boolean result = pageService.update(null, pagesUpdateWrapper);
        if (result) {
            return Response.makeOKRsp("修改页面成功");
        } else {
            return Response.makeErrRsp("修改页面失败");
        }
    }

    @ApiOperation("删除页面接口")
    @PostMapping("/deletePages")
    public ResponseResult<Object> deletePages(@RequestBody PagePages pagePages) {
        log.info("删除页面,信息是 :" + pagePages);
        UpdateWrapper<Pages> deleteWrapper = new UpdateWrapper<>();
        if (StringUtils.hasText(pagePages.getDelIds())) {
            String[] ids = pagePages.getDelIds().split(",");
            deleteWrapper.in("pageid", Arrays.asList(ids));
        }

        boolean result = pageService.remove(deleteWrapper);
        if (result) {
            return Response.makeOKRsp("删除页面成功");
        } else {
            return Response.makeErrRsp("删除页面失败");
        }
    }
}

