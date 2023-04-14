package org.bsm.controller;


import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.annotation.RefreshSession;
import org.bsm.annotation.StatisticsQPS;
import org.bsm.entity.Role;
import org.bsm.entity.User;
import org.bsm.pagemodel.PageUpdatePicture;
import org.bsm.pagemodel.PageUser;
import org.bsm.service.IRoleService;
import org.bsm.service.ISendEmailService;
import org.bsm.service.IUserService;
import org.bsm.utils.*;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
@Api(tags = "用户控制接口类")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    IUserService userService;
    @Resource
    ISendEmailService sendEmailService;

    @Resource
    IRoleService roleService;

    @Resource
    RedisUtil redisUtil;


    @StatisticsQPS
    @ApiOperation("用户注册接口")
    @PostMapping("/register")
    public ResponseResult<String> register(@RequestBody PageUser user) {
        log.info("用户开始注册,用户信息是 :" + user);
        user.setCreatetime(LocalDateTime.now());
        user.setLastmodifytime(LocalDateTime.now());
        int result = userService.registerUser(user);
        if (result == 1) {
            log.info("用户注册成功.");
            return Response.makeOKRsp("");
        } else {
            log.error("用户注册失败,用户信息是 :" + user);
            return Response.makeErrRsp("新增用户失败.");
        }
    }

    @StatisticsQPS
    @ApiOperation("用户新增接口")
    @PostMapping("/add")
    public ResponseResult<String> add(@RequestBody PageUser user) {
        log.info("用户新增,用户信息是 :" + user);
        user.setCreatetime(LocalDateTime.now());
        user.setLastmodifytime(LocalDateTime.now());
        User user1 = new User();
        BeanUtils.copyProperties(user, user1);
        byte[] saltBytes = Md5Util.getSalt(32);
        String salt = Base64Encoder.encode(saltBytes);
        user1.setSalt(salt);
        user1.setPassword(Md5Util.toPasswd(user.getPassword(), saltBytes));
        user1.setEnabled(true);
        boolean result = userService.save(user1);
        if (result) {
            log.info("用户新增成功.");
            return Response.makeOKRsp("用户新增成功.");
        } else {
            log.error("用户新增失败,用户信息是 :" + user);
            return Response.makeErrRsp("新增用户失败.");
        }
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("获取用户分页接口")
    @PostMapping("/getAlluser")
    public ResponseResult<Object> getAlluser(@RequestBody PageUser pageUser) {
        log.info("获取所有的用户,使用的查询条件是 :" + pageUser);
        User user = new User();
        BeanUtils.copyProperties(pageUser, user);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pageUser.getUsername())) {
            queryWrapper.like("username", pageUser.getUsername());
        }

        if (Objects.nonNull(pageUser.getRoleid())) {
            queryWrapper.eq("roleid", pageUser.getRoleid());
        }

        if (StringUtils.hasText(pageUser.getEmailaddress())) {
            queryWrapper.like("emailaddress", pageUser.getEmailaddress());
        }
        Page<User> page = new Page<>(pageUser.getPage().getPage(), pageUser.getPage().getPageSize());
        Page<User> userPage = userService.page(page, queryWrapper);
        return Response.makeOKRsp("获取所有的用户成功").setData(userPage);
    }

    @StatisticsQPS
    @ApiOperation("发送用户注册邮件")
    @GetMapping("/sendRegisterEmail")
    public ResponseResult<String> sendRegisterEmail(String emailaddress) {
        log.info("发送邮件的方法开始执行,发送的邮件地址是 " + emailaddress);
        boolean result = sendEmailService.sendRegisterEmail(emailaddress);
        if (result) {
            log.info("邮件发送成功 发送的邮件地址是 " + emailaddress);
            return Response.makeOKRsp("发送邮件成功");
        } else {
            log.error("邮件发送失败 发送的邮件地址是 " + emailaddress);
            return Response.makeErrRsp("发送邮件失败.");
        }
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("查询单个用户的详细信息，根据用户名")
    @GetMapping("/getUserInfo")
    public ResponseResult<Object> getUserInfo(String username) {
        log.info("查询用户的详细信息,查询的用户名是:  " + username);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userService.getOne(queryWrapper);
        return Response.makeOKRsp("查询用户信息成功").setData(user);
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("根据用户名查询用户列表")
    @PostMapping("/getUserListByUserName")
    public ResponseResult<Object> getUserListByUserName(@RequestBody PageUser pageUser) {
        
        log.info("根据用户名查询用户列表,查询的用户名是:  " + pageUser.getUsername());
        List<Pair<String, String>> userListByUserName = userService.getUserListByUserName(pageUser.getUsername());
        return Response.makeOKRsp("根据用户名查询用户列表成功").setData(userListByUserName);
    }


    @RefreshSession
    @StatisticsQPS
    @ApiOperation("查询单个用户的信息，根据sessionId")
    @GetMapping("/getUserInfoBySession")
    public ResponseResult<Object> getUserInfoBySessionId(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        log.info("查询用户的详细信息,查询的sessionId是:  " + sessionId);
        /*查询缓存中的用户信息*/
        Map<Object, Object> userInfo = redisUtil.hmget(sessionId);
        return Response.makeOKRsp("查询用户信息成功").setData(userInfo);
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("查询单个用户的详细信息，根据sessionId")
    @GetMapping("/getUserDetailInfo")
    public ResponseResult<Object> getUserDetailInfo(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        log.info("查询用户的详细信息,查询的sessionId是:  " + sessionId);
        /*查询缓存中的用户信息*/
        Map<Object, Object> userInfo = redisUtil.hmget(sessionId);
        String username = (String) userInfo.get("username");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userService.getOne(queryWrapper);
        PageUser pageUser = new PageUser();
        BeanUtils.copyProperties(user, pageUser);
        if (user.getRoleid() != null) {
            QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
            roleQueryWrapper.eq("disabled", 0);
            roleQueryWrapper.eq("roleid", user.getRoleid());
            Role role = roleService.getOne(roleQueryWrapper);
            if (role != null) {
                pageUser.setRoleName(role.getRolecname());
            }
        }
        return Response.makeOKRsp("查询用户详细信息成功").setData(pageUser);
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("删除用户接口")
    @PostMapping("/deleteUser")
    public ResponseResult<Object> deleteUser(@RequestBody PageUser pageUser) {
        log.info("删除用户,用户信息是 :" + pageUser);
        if (!StringUtils.hasText(pageUser.getUserid())) {
            return Response.makeErrRsp("删除用户参数错误！");
        }
        boolean result = userService.removeByIds(Arrays.asList(pageUser.getUserid().split(",")));
        if (result) {
            return Response.makeOKRsp("删除用户成功");
        } else {
            return Response.makeOKRsp("删除用户失败");
        }
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("用户头像上传接口")
    @PostMapping(value = "editAvatar", consumes = "multipart/*", headers = "content-type=multipart/form-data")
    public ResponseResult<Object> editAvatar(PageUpdatePicture pageUpdatePicture, HttpServletRequest req) {
        log.info("into the editAvatar function");
        try {
            String sessionId = req.getSession().getId();
            pageUpdatePicture.setSessionId(sessionId);
            String newAvatarUrl = userService.editAvatar(pageUpdatePicture);
            if (StringUtils.hasText(newAvatarUrl)) {
                return Response.makeOKRsp("修改用户头像成功").setData(newAvatarUrl);
            } else {
                return Response.makeErrRsp("修改用户头像失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return Response.makeErrRsp("修改用户头像失败");
        }
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("用户名修改接口")
    @PostMapping("/updateUserName")
    public ResponseResult<Object> updateUserName(PageUser pageUser, HttpServletRequest request) {
        if (!StringUtils.hasText(pageUser.getUsername())) {
            return Response.makeErrRsp("用户名修改失败,参数错误.").setData("");
        }
        String sessionId = request.getSession().getId();
        log.info("执行用户名修改接口,修改的用户sessionId是 :" + sessionId);
        Map<Object, Object> userInfoMap = redisUtil.hmget(sessionId);
        String username = (String) userInfoMap.get("username");
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User oldUser = userService.getOne(queryWrapper);
        /*因为用户名是唯一的,这边需要写一些校验类用于判断用户名是否修改过,如果修改过,新的用户名是否在表中出现过,这边就不做判断了*/
        oldUser.setUsername(pageUser.getUsername());
        oldUser.setLastmodifytime(LocalDateTime.now());
        /*修改缓存中的用户信息*/
        redisUtil.hset(sessionId, "username", pageUser.getUsername(), 60 * 300);
        boolean result = userService.update(oldUser, queryWrapper);
        if (result) {
            log.info("用户修改成功.");
            return Response.makeOKRsp("用户名修改成功").setData(pageUser.getUsername());
        } else {
            log.error("用户名修改失败,用户信息是 :" + pageUser);
            return Response.makeErrRsp("用户名修改失败.").setData("");
        }
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("用户密码修改接口")
    @PostMapping("/updateUserPassword")
    public ResponseResult<Object> updateUserPassword(PageUser pageUser, HttpServletRequest request) throws IOException {
        if (!StringUtils.hasText(pageUser.getPassword())) {
            return Response.makeErrRsp("用户密码修改失败,参数错误.").setData(false);
        }
        String sessionId = request.getSession().getId();
        log.info("执行用户密码修改接口,修改的用户sessionId是 :" + sessionId);
        Map<Object, Object> userInfoMap = redisUtil.hmget(sessionId);
        String username = (String) userInfoMap.get("username");

        pageUser.setUsername(username);
        /*更新用户密码*/
        boolean result = userService.editUserPassword(pageUser);
        if (result) {
            log.info("用户密码修改成功.");
            return Response.makeOKRsp("用户密码修改成功").setData(true);
        } else {
            log.error("用户密码修改失败,用户信息是 :" + pageUser);
            return Response.makeErrRsp("用户密码修改失败.").setData(false);
        }
    }

    @RefreshSession
    @StatisticsQPS
    @ApiOperation("用户密码重置接口")
    @PostMapping("/resetUserPassword")
    public ResponseResult<Object> resetUserPassword(@RequestBody PageUser pageUser) throws IOException {
        if (!StringUtils.hasText(pageUser.getUserid())) {
            return Response.makeErrRsp("用户密码重置接口,参数错误.").setData(false);
        }
        /*重置用户密码*/
        pageUser.setPassword(Constants.DEFAULT_PASSWORD);
        boolean result = userService.editUserPassword(pageUser);
        if (result) {
            log.info("用户密码重置成功.");
            return Response.makeOKRsp("用户密码重置成功").setData(true);
        } else {
            log.error("用户密码重置失败,用户信息是 :" + pageUser);
            return Response.makeErrRsp("用户密码重置失败.").setData(false);
        }
    }
}
