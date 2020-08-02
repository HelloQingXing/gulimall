package com.qx.gulimall.product.config;

import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/** @Classname CacheConfig @Description spring-cache @Date 2020/7/31 11:10 @Created by 卿星 */

// @ConfigurationProperties(prefix = "spring.cache")
//  public class CacheProperties { ……}
// 表示将缓存配置文件和当前类直接绑定，使用此注解可以获取CacheProperties中的配置信息
//@EnableConfigurationProperties(CacheProperties.class)
@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration redisConfiguration(CacheProperties cacheProperties){
        // 通过缓存配置类获取redisProperties
        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        // 获取redis默认配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        // 由于每次修改配置后，"config"的地址会换，故必须使用上一个地址接收
        // 配置value序列化规则，弃用默认的JdkSerializ，使用FastJson
        /*RedisCacheConfiguration redisCacheConfiguration*/config = config.serializeValuesWith(RedisSerializationContext
                .SerializationPair
                // 由于不知道要序列话的实体类类型，必须指定为Object
                .fromSerializer(new FastJsonRedisSerializer<>(Object.class)));
        /*System.out.println(config == redisCacheConfiguration);
        config = redisCacheConfiguration;*/
        // 重写redis配置后必须设置默认配置，不然默认会失效
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }
        return config;
    }

}
