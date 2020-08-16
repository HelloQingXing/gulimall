package com.qx.gulimall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @Classname ThreadPoolConfig
 * @Description 线程池配置类
 * @Date 2020/8/16 23:30
 * @Created by 卿星
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolProperties properties){
        return new ThreadPoolExecutor(
                properties.getCorePoolSize(),
                properties.getMaximumPoolSize(),
                properties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

}
