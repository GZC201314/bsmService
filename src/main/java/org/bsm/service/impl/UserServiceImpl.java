package org.bsm.service.impl;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.codec.Base64Encoder;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.bsm.entity.Updateimginfo;
import org.bsm.entity.User;
import org.bsm.mapper.UserMapper;
import org.bsm.pagemodel.PageUpdatePicture;
import org.bsm.pagemodel.PageUpload;
import org.bsm.pagemodel.PageUser;
import org.bsm.service.IUpdateimginfoService;
import org.bsm.service.IUserService;
import org.bsm.utils.ImgtuUtil;
import org.bsm.utils.Md5Util;
import org.bsm.utils.RedisUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2021-10-29
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    ImgtuUtil imgtuUtil;

    @Autowired
    IUpdateimginfoService updateimginfoService;

    @Override
    public Integer registerUser(PageUser pageUser) {
        /*首先判断验证码是否正确*/
        String redisValidCode = (String) redisUtil.get(pageUser.getEmailaddress());
        if (!StringUtils.hasText(redisValidCode)) {
            return 0;
        }
        if (!redisValidCode.equals(pageUser.getValidCode())) {
            return 0;
        }

        byte[] saltBytes = Md5Util.getSalt(32);
        String salt = Base64Encoder.encode(saltBytes);
        /*线程安全的*/
        pageUser.setCreatetime(LocalDateTime.now());
        pageUser.setIsfacevalid(false);
        pageUser.setLastmodifytime(LocalDateTime.now());
        pageUser.setUserid(UUID.randomUUID().toString());
        pageUser.setEnabled(true);
        pageUser.setRoleid(4);
        User user = new User();
        BeanUtils.copyProperties(pageUser, user);
        user.setSalt(salt);
        user.setPassword(Md5Util.toPasswd(pageUser.getPassword(), saltBytes));
        return userMapper.insert(user);
    }

    /**
     * 修改用户头像
     *
     * @param pageUpdatePicture 参数
     */
    @Override
    public String editAvatar(PageUpdatePicture pageUpdatePicture) throws IOException {
        if (pageUpdatePicture == null || pageUpdatePicture.getFile() == null) {
            return "";
        }

        // 向 Gitee 中提交头像
        MultipartFile avatar = pageUpdatePicture.getFile();
        /*生成文件地址*/
        String fileName = avatar.getOriginalFilename();
        if (!StringUtils.hasText(fileName)) {
            return "";
        }
        Updateimginfo updateimginfo = imgtuUtil.uploadPicture(pageUpdatePicture);
        Map<Object, Object> userInfo = redisUtil.hmget(pageUpdatePicture.getSessionId());
        String username = (String) userInfo.get("username");

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        String usericon = user.getUsericon();
        if (StringUtils.hasText(usericon)){
            // 删除原来的图片
            imgtuUtil.deletePicture(usericon);
        }
        String fileUrl = updateimginfo.getUrl();
        user.setUsericon(fileUrl);
        user.setLastmodifytime(LocalDateTime.now());
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("username", username);
        int updateResult = userMapper.update(user, updateWrapper);
        return updateResult == 1 ? fileUrl : "";
    }

    /**
     * 修改用户密码
     */
    @Override
    public boolean editUserPassword(PageUser pageUser) {
        String password = pageUser.getPassword();
        String username = pageUser.getUsername();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return false;
        }
        /*生成盐值*/
        byte[] saltBytes = Md5Util.getSalt(32);
        String salt = Base64Encoder.encode(saltBytes);
        user.setSalt(salt);
        /*加密密码*/
        user.setPassword(Md5Util.toPasswd(password, saltBytes));
        user.setLastmodifytime(LocalDateTime.now());
        int update = userMapper.update(user, queryWrapper);
        return update == 1;
    }

    /**
     * 验证用户密码
     *
     * @param pageUser 用户输入
     */
    @Override
    public boolean validateUserPassword(PageUser pageUser) throws IOException {
        String username = pageUser.getUsername();
        String password = pageUser.getPassword();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return false;
        }
        String salt = user.getSalt();
        byte[] saltBytes = Base64Decoder.decode(salt);
        String toPasswd = Md5Util.toPasswd(password, saltBytes);
        return toPasswd.equals(user.getPassword());
    }
}
