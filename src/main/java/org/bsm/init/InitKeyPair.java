package org.bsm.init;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.bsm.entity.Config;
import org.bsm.service.IConfigService;
import org.bsm.utils.RedisUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author GZC
 * @create 2022-08-15 21:13
 * @desc 初始化非对称加密密钥
 */
@Component
@Slf4j
public class InitKeyPair {
    @Resource
    private RedisUtil redisUtil;

    @Resource
    private IConfigService configService;

    @Value("${config.cache.refresh.timeout}")
    private int timeout;

    @Bean
    InitializingBean initializingBean() {
        return () -> {
            // 要执行的代码
            log.info("开始初始化加密密钥.");
            if (!redisUtil.hasKey("rsaPublicKey")) {
                RSA rsa = new RSA();
                String privateKeyBase64 = rsa.getPrivateKeyBase64();
                String publicKeyBase64 = rsa.getPublicKeyBase64();
                redisUtil.set("rsaPublicKey", publicKeyBase64);
                redisUtil.set("rsaPrivateKey", privateKeyBase64);
            }
            if (!redisUtil.hasKey("aesKey")) {
                SecretKey secretKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), 128);
                byte[] encoded = secretKey.getEncoded();
                String aesKey = HexUtil.encodeHexStr(encoded);
                redisUtil.set("aesKey", aesKey);
            }
        };
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            // 要执行的代码
        };
    }

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            // 要执行的代码

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    log.info("开始刷新配置缓存.");
                    refreshConfig();
                }
            };
            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("configautorefresh-schedule-pool-%d").daemon(true).build());
            scheduledThreadPoolExecutor.scheduleWithFixedDelay(timerTask, 0, 300, TimeUnit.SECONDS);

        };
    }

    public void refreshConfig() {
        List<Config> list = configService.list();
        redisUtil.del("bsm_config");
        for (Config config : list) {
            redisUtil.hset("bsm_config", config.getName(), config.getValue());
        }

    }
}
