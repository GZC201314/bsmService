package org.bsm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author GZC
 */
@SpringBootApplication
@MapperScan("org.bsm.mapper")
public class BsmServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(BsmServiceApplication.class, args);
  }
}
