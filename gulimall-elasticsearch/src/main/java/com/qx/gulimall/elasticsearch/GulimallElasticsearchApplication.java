package com.qx.gulimall.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GulimallElasticsearchApplication {

  public static void main(String[] args) {
    SpringApplication.run(GulimallElasticsearchApplication.class, args);
  }
}
