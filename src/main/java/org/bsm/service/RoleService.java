package org.bsm.service;

import org.bsm.model.Role;

/**
 * @author GZC
 * @create 2021-10-27 16:52
 * @desc Role Srevice Interface
 */
public interface RoleService {
    /**
     * add a role
     * @param role user input
     * @return implemented row number
     */
    int add(Role role);

    /**
     * update role
     * @param role user input
     * @return implemented row number
     */
    int update(Role role);

    /**
     * delete role by id
     * @param roleId roleId
     * @return implemented row number
     */
    int deleteById(Integer roleId);

    /**
     * query role by id
     * @param roleId roleId
     * @return Role instance
     */
    Role queryRoleById(Integer roleId);
}
