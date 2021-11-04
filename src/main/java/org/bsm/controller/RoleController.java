package org.bsm.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.Role;
import org.bsm.pagemodel.PageRole;
import org.bsm.service.impl.RoleServiceImpl;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 角色控制器
 * </p>
 *
 * @author GZC
 * @since 2021-10-29
 */
@Api(tags = "角色控制接口类")
@RestController
@Slf4j
@RequestMapping("/role")
public class RoleController {

    @Autowired
    RoleServiceImpl roleService;

    @ApiOperation("获取所有用户角色接口")
    @GetMapping("/getAllRole")
    public ResponseResult<Object> getAllRole(PageRole pageRole) {
        log.info("获取所有的用户角色,使用的查询条件是 :" + pageRole);
        Role role = new Role();
        BeanUtils.copyProperties(pageRole, role);
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pageRole.getRolecname())) {
            queryWrapper.like("rolecname", pageRole.getRolecname());
        }
        List<Role> roleList = roleService.list(queryWrapper);
        return Response.makeOKRsp("获取所有的用户角色成功").setData(roleList);
    }

    @ApiOperation("获取所有用户角色接口(分页)")
    @GetMapping("/getPageRole")
    public ResponseResult<Object> getPageRole(PageRole pageRole) {
        log.info("获取所有的用户角色(分页),使用的查询条件是 :" + pageRole);
        Role role = new Role();
        BeanUtils.copyProperties(pageRole, role);
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pageRole.getRolecname())) {
            queryWrapper.like("rolecname", pageRole.getRolecname());
        }
        Page<Role> page = new Page<>(pageRole.getCurrent(), pageRole.getSize());
        Page<Role> rolePage = roleService.page(page, queryWrapper);
        return Response.makeOKRsp("获取所有的用户角色(分页)成功").setData(rolePage);
    }

    @ApiOperation("获取用户角色详细信息接口")
    @GetMapping("/getRoleInfo")
    public ResponseResult<Object> getRoleInfo(PageRole pageRole) {
        log.info("获取用户角色详细信息,使用的查询条件是 :" + pageRole);
        Role role = new Role();
        BeanUtils.copyProperties(pageRole, role);
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        if (pageRole.getRoleid() != null) {
            queryWrapper.eq("roleid", pageRole.getRoleid());
        }
        role = roleService.getOne(queryWrapper);
        return Response.makeOKRsp("获取用户角色详细信息成功").setData(role);
    }

    @ApiOperation("新增用户角色接口")
    @PostMapping("/addRole")
    public ResponseResult<Object> addRole(Role pageRole) {
        log.info("新增用户角色,角色信息是 :" + pageRole);
        boolean result = roleService.save(pageRole);
        if (result) {
            return Response.makeOKRsp("新增用户角色成功");
        } else {
            return Response.makeOKRsp("新增用户角色失败");
        }
    }

    @ApiOperation("修改用户角色接口")
    @PostMapping("/updateRole")
    public ResponseResult<Object> updateRole(Role pageRole) {
        log.info("新增用户角色,角色信息是 :" + pageRole);
        boolean result = roleService.saveOrUpdate(pageRole);
        if (result) {
            return Response.makeOKRsp("修改用户角色成功");
        } else {
            return Response.makeOKRsp("修改用户角色失败");
        }
    }

    @ApiOperation("删除用户角色接口(逻辑删除)")
    @DeleteMapping("/deleteRole")
    public ResponseResult<Object> deleteRole(Role pageRole) {
        pageRole.setDisabled(true);
        log.info("删除用户角色,角色信息是 :" + pageRole);
        UpdateWrapper<Role> updateWrapper = new UpdateWrapper<>();
        if (pageRole.getRoleid() != null) {
            updateWrapper.eq("roleid", pageRole.getRoleid());
        }

        boolean result = roleService.update(pageRole, updateWrapper);
        if (result) {
            return Response.makeOKRsp("修改用户角色成功");
        } else {
            return Response.makeOKRsp("修改用户角色失败");
        }
    }
}

