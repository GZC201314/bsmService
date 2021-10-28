package org.bsm.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.bsm.config.MyPasswordEncoder;
//import org.bsm.model.User;
import org.bsm.model.Role;
import org.bsm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author GZC
 */
@Configuration
public class UserDetailServiceImpl implements UserDetailsService {

    private PasswordEncoder passwordEncoder = new MyPasswordEncoder();

    @Autowired
    RoleServiceImpl roleService;
    @Autowired
    UserServiceImpl userService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 模拟一个用户，替代数据库获取逻辑
        if(StringUtils.isBlank(username)){
            throw new UsernameNotFoundException("未找到当前用户.");
        }
        org.bsm.model.User user = userService.queryUserByName(username);
        if(user == null){
            throw new UsernameNotFoundException("未找到当前用户.");
        }
        /*获取用户角色*/
        Role role = roleService.queryRoleById(user.getRoleid());
        /*这里把加密后的免密和盐值一起传入,用于后面的免密校验*/
        return new User(username, user.getPassword() + "&" + user.getSalt(), user.getEnabled(),
                true, true,
                true, AuthorityUtils.commaSeparatedStringToAuthorityList(role.getRolename()));
    }
}
