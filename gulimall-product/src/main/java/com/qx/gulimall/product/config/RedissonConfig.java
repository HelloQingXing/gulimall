package com.qx.gulimall.product.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname RedissonConfig
 * @Description redisson配置类
 * @Date 2020/7/29 21:03
 * @Created by 卿星
 */
@Configuration
public class RedissonConfig {

    /**
     * 所有对Redisson的使用都是通过RedissonClient对象
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient getRedissonClient(){
        // 创建配置
        Config config = new Config();
        // 设置单节点模式，
        SingleServerConfig singleServer = config.useSingleServer();
        // 设置主机地址 Redis url should start with redis:// or rediss:// (for SSL connection)  SSL：表示安全的连接
        // 必须要带上redis://和端口号port 否则会报IllegalArgumentException(redis://)异常和StringIndexOutOfBoundsException异常（-port）
        singleServer.setAddress("redis://192.168.56.10:6379");
        // 创建redisson
        RedissonClient redissonClient = Redisson.create(config);

        return redissonClient;
    }


}
