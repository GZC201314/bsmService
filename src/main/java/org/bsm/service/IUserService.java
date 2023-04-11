package org.bsm.service;

import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.extension.service.IService;
import org.bsm.entity.User;
import org.bsm.pagemodel.PageUpdatePicture;
import org.bsm.pagemodel.PageUser;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
public interface IUserService extends IService<User> {

    Integer registerUser(PageUser pageUser);

    /**
     * 修改用户头像
     */
    String editAvatar(PageUpdatePicture pageUpload) throws IOException;

    /**
     * 修改用户密码
     */
    boolean editUserPassword(PageUser pageUser) throws IOException;

    /**
     * 修改用户密码
     */
    boolean validateUserPassword(PageUser pageUser) throws IOException;

    /**
     * 根据用户名查询用户列表
     */
    List<Pair<String, String>> getUserListByUserName(String userName);

}
