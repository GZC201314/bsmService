package org.bsm.controller;


import org.bsm.entity.User;
import org.bsm.service.impl.UserServiceImpl;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @GetMapping("/userAdd")
    public ResponseResult<String> userAdd(User user){
        int result = userService.registerUser(user);
        if(result ==1){
            return Response.makeOKRsp("") ;
        }else {
            return Response.makeErrRsp("新增用户失败.");
        }
    }
}
