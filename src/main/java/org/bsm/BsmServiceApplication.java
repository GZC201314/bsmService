package org.bsm;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * @author GZC
 */
@SpringBootApplication
@EnableEncryptableProperties
@EnableScheduling
public class BsmServiceApplication {

    public static void main(String[] args) {

        SpringApplication.run(BsmServiceApplication.class, args);
        //解决WebSocket不能注入的问题
    }
}
