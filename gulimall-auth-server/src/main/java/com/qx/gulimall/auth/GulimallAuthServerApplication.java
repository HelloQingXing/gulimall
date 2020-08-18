package com.qx.gulimall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

// 要调用其他微服务，必须开启此注解
@EnableFeignClients
@SpringBootApplication
public class GulimallAuthServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(GulimallAuthServerApplication.class, args);
  }
}
