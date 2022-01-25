package org.bsm;

import org.bsm.entity.User;
import org.bsm.pagemodel.PageUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author GZC
 * @create 2021-11-03 16:10
 * @desc
 */
public class CopyValueTest {
    @Test
    public void copyValue() {
        User user = new User();
        user.setUserid(UUID.randomUUID().toString());
        user.setUsername("GZC");
        user.setCreatetime(LocalDateTime.now());
        PageUser pageUser = new PageUser();
        pageUser.setUsername("admin");
//        BeanUtils.copyProperties(pageUser, user);
        BeanUtils.copyProperties(pageUser, user, "userid", "createtime");
        System.out.println("复制后的用户信息是 :" + user);
    }
    
}
