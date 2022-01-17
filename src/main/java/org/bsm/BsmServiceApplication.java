package org.bsm;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;


/**
 * @author GZC
 */
@SpringBootApplication
//@MapperScan("org.bsm.mapper")
@EnableEncryptableProperties
public class BsmServiceApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(BsmServiceApplication.class);

        ConfigurableApplicationContext configurableApplicationContext = springApplication.run(args);


    }
}
