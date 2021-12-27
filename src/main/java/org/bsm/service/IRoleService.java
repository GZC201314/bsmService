package org.bsm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.bsm.entity.Role;
import org.bsm.pagemodel.PageRole;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
public interface IRoleService extends IService<Role> {
    public boolean editRoleStatus(PageRole pageRole);

    public boolean delRoles(PageRole pageRole);

}
