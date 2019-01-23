package com.github.sunnus3.example.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author: zhangwei
 * @date: 14:43/2019-01-23
 */
@SpringBootApplication
@Import(value = {UserServiceImpl.class})
public class AopApplication extends AbstractApplication {


    public static void main(String[] args) {
        SpringApplication.run(AopApplication.class, args);
    }
}
