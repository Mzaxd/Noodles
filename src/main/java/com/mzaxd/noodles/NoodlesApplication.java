package com.mzaxd.noodles;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author root
 */
@SpringBootApplication
@EnableScheduling
@MapperScan("com.mzaxd.noodles.mapper")
public class NoodlesApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoodlesApplication.class, args);
    }

}
