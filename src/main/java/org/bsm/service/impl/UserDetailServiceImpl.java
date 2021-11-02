package org.bsm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bsm.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author GZC
 */
@Slf4j
@Configuration
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    RoleServiceImpl roleService;
    @Autowired
    UserServiceImpl userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 模拟一个用户，替代数据库获取逻辑
        if (StringUtils.isBlank(username)) {
            log.error("未找到当前用户.");
            throw new UsernameNotFoundException("未找到当前用户.");
        }
        QueryWrapper<org.bsm.entity.User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        org.bsm.entity.User user = userService.getOne(wrapper);
        if (user == null) {
            log.error("未找到当前用户.");
            throw new UsernameNotFoundException("未找到当前用户.");
        }
        /*获取用户角色*/
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper.eq("roleId", user.getRoleid());
        Role role = roleService.getOne(roleQueryWrapper);
        /*这里把加密后的免密和盐值一起传入,用于后面的免密校验*/
        return new User(username, user.getPassword() + "&" + user.getSalt(), user.getEnabled(),
                true, true,
                true, AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_" + role.getRolename()));
    }
}
