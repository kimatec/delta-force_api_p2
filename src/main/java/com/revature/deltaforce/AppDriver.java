package com.revature.deltaforce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AppDriver {
    public static void main(String[] args) {
        SpringApplication.run(AppDriver.class, args);
    }

}
