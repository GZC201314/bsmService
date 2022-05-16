package org.bsm;

import cn.hutool.crypto.asymmetric.RSA;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.bsm.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;


/**
 * @author GZC
 */
@SpringBootApplication
@EnableEncryptableProperties
@EnableScheduling
public class BsmServiceApplication {
    @Autowired
    private RedisUtil redisUtil;

    public static void main(String[] args) {

        SpringApplication.run(BsmServiceApplication.class, args);


//        SpringApplication springApplication = new SpringApplication(BsmServiceApplication.class);
//
//        ConfigurableApplicationContext configurableApplicationContext = springApplication.run(args);


    }

    @PostConstruct
    public void generateKeyPair() {
        RSA rsa = new RSA();
        String privateKeyBase64 = rsa.getPrivateKeyBase64();
        String publicKeyBase64 = rsa.getPublicKeyBase64();
        redisUtil.set("rsaPublicKey", publicKeyBase64);
        redisUtil.set("rsaPrivateKey", privateKeyBase64);
    }
}
