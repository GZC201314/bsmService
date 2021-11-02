package org.bsm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.bsm.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
public interface IUserService extends IService<User> {
    public Integer registerUser(User user);
}
