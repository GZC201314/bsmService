package org.bsm.controller;

import org.bsm.model.User;
import org.bsm.service.impl.UserServiceImpl;
import org.bsm.utils.Constants;
import org.bsm.utils.Response;
import org.bsm.utils.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author GZC
 * @create 2021-10-25 10:08
 * @desc user controller
 */
@RestController
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @GetMapping("/userAdd")
    public ResponseResult<String> userAdd(User user){
        int result = userService.add(user);
        if(result ==1){
            return Response.makeOKRsp("") ;
        }else {
            return Response.makeErrRsp("新增用户失败.");
        }
    }
}
