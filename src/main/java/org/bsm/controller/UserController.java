package org.bsm.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.bsm.entity.User;
import org.bsm.pagemodel.PageUser;
import org.bsm.service.impl.SendEmailServiceImpl;
import org.bsm.service.impl.UserServiceImpl;
import org.bsm.utils.RedisUtil;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;

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
    @Autowired
    UserServiceImpl userService;
    @Autowired
    SendEmailServiceImpl sendEmailService;

    @Autowired
    RedisUtil redisUtil;

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

    @ApiOperation("获取用户分页接口")
    @GetMapping("/getAlluser")
    public ResponseResult<Object> getAlluser(PageUser pageUser) {
        log.info("获取所有的用户,使用的查询条件是 :" + pageUser);
        User user = new User();
        BeanUtils.copyProperties(pageUser, user);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.hasText(pageUser.getUsername())) {
            queryWrapper.like("username", pageUser.getUsername());
        }
        if (StringUtils.hasText(pageUser.getEmailaddress())) {
            queryWrapper.like("emailaddress", pageUser.getEmailaddress());
        }
        Page<User> page = new Page<>(pageUser.getCurrent(), pageUser.getSize());
        Page<User> userPage = userService.page(page, queryWrapper);
        return Response.makeOKRsp("获取所有的用户成功").setData(userPage);
    }

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

    @ApiOperation("用户修改接口")
    @PostMapping("/update")
    public ResponseResult<String> update(PageUser pageUser) {
        log.info("还是执行用户修改接口,修改的用户信息是 :" + pageUser);
        UpdateWrapper<User> queryWrapper = new UpdateWrapper<>();
        queryWrapper.eq("userid", pageUser.getUserid());
        /*因为用户名是唯一的,这边需要写一些校验类用于判断用户名是否修改过,如果修改过,新的用户名是否在表中出现过,这边就不做判断了*/
        User user = new User();
        BeanUtils.copyProperties(pageUser, user);
        user.setLastmodifytime(LocalDateTime.now());
        boolean result = userService.update(user, queryWrapper);
        if (result) {
            log.info("用户修改成功.");
            return Response.makeOKRsp("");
        } else {
            log.error("用户修改失败,用户信息是 :" + pageUser);
            return Response.makeErrRsp("用户修改失败.");
        }
    }

    @ApiOperation("查询单个用户的详细信息，根据用户名")
    @GetMapping("/getUserInfo")
    public ResponseResult<Object> getUserInfo(String username) {
        log.info("查询用户的详细信息,查询的用户名是:  " + username);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userService.getOne(queryWrapper);
        return Response.makeOKRsp("查询用户信息成功").setData(user);
    }

    @ApiOperation("查询单个用户的详细信息，根据sessionId")
    @GetMapping("/getUserInfoBySession")
    public ResponseResult<Object> getUserInfoBySessionId(HttpServletRequest request) {
        String sessionId = request.getSession().getId();
        log.info("查询用户的详细信息,查询的sessionId是:  " + sessionId);
        /*查询缓存中的用户信息*/
        Map<Object, Object> userInfo = redisUtil.hmget(sessionId);
        return Response.makeOKRsp("查询用户信息成功").setData(userInfo);
    }

    @ApiOperation("删除用户接口(逻辑删除)")
    @DeleteMapping("/deleteUser")
    public ResponseResult<Object> deleteUser(User pageUser) {
        log.info("删除用户角色,角色信息是 :" + pageUser);
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        if (StringUtils.hasText(pageUser.getUserid())) {
            updateWrapper.eq("userid", pageUser.getUserid());
        }
        pageUser.setEnabled(false);
        pageUser.setLastmodifytime(LocalDateTime.now());
        boolean result = userService.update(pageUser, updateWrapper);
        if (result) {
            return Response.makeOKRsp("删除用户成功");
        } else {
            return Response.makeOKRsp("删除用户失败");
        }
    }


//    @ApiOperation("用户头像上传接口")
//    @PostMapping(value = "ocr", consumes = "multipart/*", headers = "content-type=multipart/form-data")
//    public ResponseResult<Object> orc(PageUpload pageUpload) {
//        log.info("into the ocr function");
//        try {
//            String result = aiService.ocr(pageUpload);
//            return Response.makeOKRsp("图片识别成功").setData(result);
//        } catch (Exception e) {
//            log.error(e.getMessage());
//            return Response.makeErrRsp("图片识别失败");
//        }
//    }

}
