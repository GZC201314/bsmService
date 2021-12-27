package org.bsm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.bsm.entity.Role;
import org.bsm.mapper.RoleMapper;
import org.bsm.pagemodel.PageRole;
import org.bsm.service.IRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

    @Autowired
    RoleMapper roleMapper;

    @Override
    public boolean editRoleStatus(PageRole pageRole) {
        if (pageRole == null || pageRole.getRoleid() == null || pageRole.getDisabled() == null) {
            return false;
        }
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper.eq("roleid", pageRole.getRoleid());
        Role role = roleMapper.selectOne(roleQueryWrapper);
        role.setDisabled(pageRole.getDisabled());
        int update = roleMapper.updateById(role);
        return update == 1;
    }

    @Override
    public boolean delRoles(PageRole pageRole) {
        QueryWrapper<Role> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper.in("roleid", Arrays.asList(pageRole.getDelIds().split(",")));
        List<Role> roles = roleMapper.selectList(roleQueryWrapper);
        for (Role role :
                roles) {
            role.setIsdeleted(true);
            roleMapper.updateById(role);
        }
        return true;
    }
}
