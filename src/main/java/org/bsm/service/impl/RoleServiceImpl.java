package org.bsm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.bsm.mapper.RoleMapper;
import org.bsm.model.Role;
import org.bsm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author GZC
 * @create 2021-10-27 16:55
 * @desc Role Service Impl
 */
@Service("roleService")
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleMapper roleMapper;

    @Override
    public int add(Role role) {
        return roleMapper.insert(role);
    }

    @Override
    public int update(Role role) {
        return  roleMapper.updateById(role);
    }

    @Override
    public int deleteById(Integer roleId) {
        QueryWrapper<Role> queryParam = new QueryWrapper<>();
        queryParam.eq("roleId",roleId);
        return roleMapper.delete(queryParam);
    }

    @Override
    public Role queryRoleById(Integer roleId) {
        QueryWrapper<Role> queryParam = new QueryWrapper<>();
        queryParam.eq("roleId",roleId);
        return roleMapper.selectOne(queryParam);
    }
}
