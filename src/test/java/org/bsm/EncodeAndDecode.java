package org.bsm;

import com.baomidou.mybatisplus.core.toolkit.AES;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * @author GZC
 * @create 2021-11-05 10:59
 * @desc
 */

@Disabled
public class EncodeAndDecode {
    @Test
    public void encodeAndDecode() {
        // 生成 16 位随机 AES 密钥
        String randomKey = "";

//        System.out.println(AES.decrypt("VMdY+m3qH+xaTIhPNcYkjg==", randomKey));

        // 随机密钥加密
//        System.out.println("数据库Url:");
        System.out.println("mpw:" + AES.decrypt("jdbc:mysql://127.0.0.1:12306/flowable?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8", randomKey));
//        System.out.println("数据库username:");
//        System.out.println("mpw:" + AES.encrypt("root", randomKey));
//        System.out.println("数据库password:");
        System.out.println("mpw:" + AES.encrypt("123456", randomKey));
//        System.out.println("druid password:");
//        System.out.println("mpw:" + AES.encrypt("druid123", randomKey));
//        System.out.println("redis password:");
//        System.out.println("mpw:" + AES.encrypt("GZCabc123", randomKey));
//        System.out.println("email password:");
//        System.out.println("mpw:" + AES.encrypt("abc123", randomKey));
//        System.out.println("email username:");
//        System.out.println(AES.encrypt("jdbc:mysql://127.0.0.1:3306/bsm?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8", randomKey));

//        String result = AES.encrypt("Hello world", randomKey);
//
//        System.out.println(AES.decrypt(result, randomKey));
    }
}
