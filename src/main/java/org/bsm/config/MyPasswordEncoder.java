package org.bsm.config;

import lombok.SneakyThrows;
import org.bsm.utils.Md5Utils;
import org.springframework.security.crypto.password.PasswordEncoder;
import sun.misc.BASE64Decoder;

/**
 * @author GZC
 * @create 2021-10-27 15:52
 * @desc my password encode class
 */
public class MyPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return Md5Utils.toPasswd((String)rawPassword);
    }

    @SneakyThrows
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        /*在这边检验密码,因为密码是带有salt的你可以在传入加密的密码的时候拼接上salt*/
        String[] strings = encodedPassword.split("&");
        String encodePassword = strings[0];
        String salt =strings[1];
        BASE64Decoder decoder = new BASE64Decoder();
        return encodePassword.equals(Md5Utils.toPasswd((String) rawPassword,decoder.decodeBuffer(salt)));
    }
}
