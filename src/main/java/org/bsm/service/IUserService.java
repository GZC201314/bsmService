package org.bsm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.bsm.entity.User;
import org.bsm.pagemodel.PageUpload;
import org.bsm.pagemodel.PageUser;

import java.io.IOException;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
public interface IUserService extends IService<User> {

    public Integer registerUser(PageUser pageUser);

    /**
     * 修改用户头像
     */
    public String editAvatar(PageUpload pageUpload) throws IOException;

    /**
     * 修改用户密码
     */
    public boolean editUserPassword(PageUser pageUser) throws IOException;

}
