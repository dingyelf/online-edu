package com.edu.online;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.edu.online.mapper")
public class OnlineEduApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineEduApplication.class, args);
    }

}
