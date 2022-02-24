package org.bsm.config;

import cn.hutool.core.codec.Base64Decoder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bsm.utils.Md5Util;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author GZC
 * @create 2021-10-27 15:52
 * @desc spring security my password encode class
 */
@Slf4j
public class MyPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        log.info("开始加密用户密码.");

        return Md5Util.toPasswd((String) rawPassword);
    }

    @SneakyThrows
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        /*在这边检验密码,因为密码是带有salt的你可以在传入加密的密码的时候拼接上salt*/
        String[] strings = encodedPassword.split("&");
        if (strings.length != 2) {
            return false;
        }
        String encodePassword = strings[0];
        String salt = strings[1];
        return encodePassword.equals(Md5Util.toPasswd((String) rawPassword, Base64Decoder.decode(salt)));
    }
}
