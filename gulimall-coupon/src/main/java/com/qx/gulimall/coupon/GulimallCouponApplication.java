package com.qx.gulimall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.qx.gulimall.coupon.dao")
// nacos注册发现（可以不写）
//@EnableDiscoveryClient
// 启用openfeign远程调用
@EnableFeignClients(basePackages = {"com.qx.gulimall.coupon"})
// 开启事务
@EnableTransactionManagement
public class GulimallCouponApplication {

  public static void main(String[] args) {
    SpringApplication.run(GulimallCouponApplication.class, args);
  }
}
