package org.bsm.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.Doubanbook;
import org.bsm.pagemodel.PageDouBanBook;
import org.bsm.service.IDoubanbookService;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * <p>
 * 书本表 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-02-14
 */
@Slf4j
@RestController
@RequestMapping("/doubanbook")
@Api(tags = "豆瓣图书接口类")
public class DoubanbookController {
    @Autowired
    IDoubanbookService doubanbookService;

    @ApiOperation("获取豆瓣图书接口(分页)")
    @PostMapping("/getPageDouBanBook")
    public ResponseResult<Object> getPageDouBanBook(@RequestBody PageDouBanBook pageDouBanBook) {
        log.info("获取豆瓣图书接口(分页),使用的查询条件是 :" + pageDouBanBook);
        Doubanbook doubanbook = new Doubanbook();
        BeanUtils.copyProperties(pageDouBanBook, doubanbook);
        QueryWrapper<Doubanbook> queryWrapper = new QueryWrapper<>();
        /*在这边进行查询操作，但是需要指定查询的列*/
        if (StringUtils.hasText(pageDouBanBook.getPage().getSearch())) {
            queryWrapper.like("isbn", pageDouBanBook.getPage().getSearch());
            queryWrapper.or().eq("name", pageDouBanBook.getPage().getSearch());
        }

        if (pageDouBanBook.getPage() == null) {
            return Response.makeErrRsp("参数错误");
        }
        Page<Doubanbook> page = new Page<>(pageDouBanBook.getPage().getPage(), pageDouBanBook.getPage().getPageSize());
        Page<Doubanbook> rolePage = doubanbookService.page(page, queryWrapper);
        return Response.makeOKRsp("获取豆瓣图书接口(分页)成功").setData(rolePage);
    }

    @ApiOperation("根据ISBN,获取豆瓣图书详细信息接口")
    @GetMapping("/getDouBanBookInfo")
    public ResponseResult<Object> getDouBanBookInfo(PageDouBanBook pageDouBanBook) {
        log.info("获取豆瓣图书详细信息,使用的查询条件是 :" + pageDouBanBook);
        Doubanbook doubanbook = new Doubanbook();
        BeanUtils.copyProperties(pageDouBanBook, doubanbook);
        QueryWrapper<Doubanbook> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pageDouBanBook.getIsbn())) {
            queryWrapper.eq("isbn", pageDouBanBook.getIsbn());
        }
        doubanbook = doubanbookService.getOne(queryWrapper);
        return Response.makeOKRsp("获取豆瓣图书详细信息成功").setData(doubanbook);
    }

    @ApiOperation("新增豆瓣图书接口")
    @PostMapping("/addDoubanBook")
    public ResponseResult<Object> addDoubanBook(@RequestBody Doubanbook doubanbook) {
        log.info("新增豆瓣图书接口,信息是 :" + doubanbook);
        boolean result = doubanbookService.save(doubanbook);
        if (result) {
            return Response.makeOKRsp("新增豆瓣图书成功");
        } else {
            return Response.makeOKRsp("新增豆瓣图书失败");
        }
    }

    @ApiOperation("修改豆瓣图书接口")
    @PostMapping("/updateDoubanBook")
    public ResponseResult<Object> DoubanBook(@RequestBody Doubanbook doubanbook) {
        log.info("修改豆瓣图书,信息是 :" + doubanbook);
        boolean result = doubanbookService.saveOrUpdate(doubanbook);
        if (result) {
            return Response.makeOKRsp("修改豆瓣图书成功");
        } else {
            return Response.makeOKRsp("修改豆瓣图书失败");
        }
    }

    @ApiOperation("删除豆瓣图书接口")
    @PostMapping("/deleteDouBanBook")
    public ResponseResult<Object> deleteDouBanBook(@RequestBody PageDouBanBook pageDouBanBook) {
        log.info("删除豆瓣图书,信息是 :" + pageDouBanBook);
        if (!StringUtils.hasText(pageDouBanBook.getDelIds())) {
            return Response.makeErrRsp("参数错误");
        }
        String[] delIds = pageDouBanBook.getDelIds().split(",");


        boolean result = doubanbookService.removeByIds(Arrays.asList(delIds));
        if (result) {
            return Response.makeOKRsp("删除豆瓣图书成功");
        } else {
            return Response.makeOKRsp("删除豆瓣图书失败");
        }
    }

    @ApiOperation("校验ISBN号是否唯一")
    @GetMapping("/validISBN")
    public ResponseResult<Object> validISBN(PageDouBanBook pageDouBanBook) {
        log.info("校验ISBN号是否唯一,使用的查询条件是 :" + pageDouBanBook);
        QueryWrapper<Doubanbook> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pageDouBanBook.getIsbn())) {
            queryWrapper.eq("isbn", pageDouBanBook.getIsbn());
        }
        int count = doubanbookService.count(queryWrapper);
        return Response.makeOKRsp("校验ISBN号成功").setData(count == 0);
    }

}

