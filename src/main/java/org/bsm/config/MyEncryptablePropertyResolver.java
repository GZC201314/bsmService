package org.bsm.config;

import com.baomidou.mybatisplus.core.toolkit.AES;
import com.ulisesbocchio.jasyptspringboot.EncryptablePropertyResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

/**
 * @author GZC
 * @create 2021-11-05 13:50
 * @desc 自定义配置解密处理类
 */
@Slf4j
public class MyEncryptablePropertyResolver implements EncryptablePropertyResolver {

    @Value("${mpw.key}")
    private String bsmKey;


    @Override
    public String resolvePropertyValue(String s) {
//        if (!StringUtils.hasText(bsmKey)) {
//            log.error("你没有设置启动命令行参数 mpw.key!");
//        }
        if (StringUtils.hasText(s) && s.startsWith(MyEncryptablePropertyDetector.ENCODED_PASSWORD_HINT)) {
            return AES.decrypt(s.substring(MyEncryptablePropertyDetector.ENCODED_PASSWORD_HINT.length()), bsmKey);
        }
        return s;
    }
}
