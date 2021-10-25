package org.bsm.controller;

import org.bsm.dao.TuserDao;
import org.bsm.model.Tuser;
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
    TuserDao tuserDao;

    @GetMapping("/queryUser")
    public Tuser queryUser(String name){
        return tuserDao.selectByPrimaryKey(name);
    }
}
