package org.bsm.service;

import org.bsm.model.Role;
import org.bsm.model.User;

/**
 * @author GZC
 * @create 2021-10-27 16:34
 * @desc user service interface
 */
public interface UserService {
    /**
     * add a user
     * @param user user input
     * @return implemented row number
     */
    int add(User user);

    /**
     * update user
     * @param user user input
     * @return implemented row number
     */
    int update(User user);

    /**
     * delete user by id
     * @param userId userId
     * @return implemented row number
     */
    int deleteById(String userId);

    /**
     * query user by name
     * @param name userId
     * @return User instance
     */
    User queryUserByName(String name);
    /**
     * query user by id
     * @param userId userId
     * @return User instance
     */
    User queryUserById(String userId);
}
