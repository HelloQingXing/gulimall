package com.qx.gulimall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Classname ThreadPoolProperties
 * @Description 线程池配置参数
 * @Date 2020/8/16 23:37
 * @Created by 卿星
 */
@ConfigurationProperties("gulimall.thread.pool")
@Component
@Data
public class ThreadPoolProperties {
    private Integer corePoolSize;
    private Integer maximumPoolSize;
    private Long keepAliveTime;
}
