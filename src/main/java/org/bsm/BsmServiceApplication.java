package org.bsm;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;


/**
 * @author GZC
 */
@SpringBootApplication
//@MapperScan("org.bsm.mapper")
@EnableEncryptableProperties
public class BsmServiceApplication {
    public static void main(String[] args) {
        System.out.println("args  ===:" + Arrays.toString(args));
        SpringApplication.run(BsmServiceApplication.class, args);
    }
}
