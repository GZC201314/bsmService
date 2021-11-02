package org.bsm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.bsm.entity.Role;
import org.bsm.mapper.RoleMapper;
import org.bsm.service.IRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements IRoleService {

}