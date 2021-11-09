package org.bsm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.bsm.entity.User;
import org.bsm.mapper.UserMapper;
import org.bsm.pagemodel.PageUser;
import org.bsm.service.IUserService;
import org.bsm.utils.Md5Utils;
import org.bsm.utils.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Encoder;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public Integer registerUser(PageUser pageUser) {
        /*首先判断验证码是否正确*/
        String redisValidCode = (String) redisUtil.get(pageUser.getEmailaddress());
        if (!StringUtils.hasText(redisValidCode)) {
            return 0;
        }
        if (!redisValidCode.equals(pageUser.getValidCode())) {
            return 0;
        }

        BASE64Encoder encode = new BASE64Encoder();
        String userName = pageUser.getUsername();
        String password = pageUser.getPassword();
        byte[] saltBytes = Md5Utils.getSalt(32);
        String salt = encode.encode(saltBytes);
        pageUser.setSalt(salt);
        /*线程安全的*/
        pageUser.setCreatetime(LocalDateTime.now());
        pageUser.setIsfacevalid(false);
        pageUser.setLastmodifytime(LocalDateTime.now());
        pageUser.setUserid(UUID.randomUUID().toString());
        pageUser.setEnabled(true);
        pageUser.setRoleid(4);
        pageUser.setPassword(Md5Utils.toPasswd(pageUser.getPassword(), saltBytes));
        User user = new User();
        BeanUtils.copyProperties(pageUser, user);
        return userMapper.insert(user);
    }
}
