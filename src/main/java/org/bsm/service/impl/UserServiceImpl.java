package org.bsm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.bsm.mapper.UserMapper;
import org.bsm.model.User;
import org.bsm.service.UserService;
import org.bsm.utils.Md5Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * @author GZC
 * @create 2021-10-27 17:21
 * @desc User Service Impl calss
 */
@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    UserMapper userMapper;
    /**
     * add a user
     *
     * @param user user input
     * @return implemented row number
     */
    @Override
    public int add(User user) {
        BASE64Encoder encode = new BASE64Encoder();
        String userName = user.getUsername();
        String password = user.getPassword();
        byte[] saltBytes = Md5Utils.getSalt(8);
        String salt = encode.encode(saltBytes);
        user.setSalt(salt);
        user.setCreatetime(new Date());
        user.setIsfacevalid(false);
        user.setLastmodifytime(new Date());
        user.setUserid(UUID.randomUUID().toString());
        user.setEnabled(true);
        user.setRoleid(1);
        user.setPassword(Md5Utils.toPasswd(user.getPassword(),saltBytes));
        return userMapper.insert(user);
    }

    /**
     * update user
     *
     * @param user user input
     * @return implemented row number
     */
    @Override
    public int update(User user) {
        return userMapper.updateById(user);
    }

    /**
     * delete user by id
     *
     * @param userId userId
     * @return implemented row number
     */
    @Override
    public int deleteById(String userId) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("userId",userId);
        return userMapper.delete(wrapper);
    }

    /**
     * query user by name
     *
     * @param name userId
     * @return User instance
     */
    @Override
    public User queryUserByName(String name) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("userName",name);
        return userMapper.selectOne(wrapper);
    }

    /**
     * query user by id
     *
     * @param userId userId
     * @return User instance
     */
    @Override
    public User queryUserById(String userId) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("userId",userId);
        return userMapper.selectOne(wrapper);
    }
}
