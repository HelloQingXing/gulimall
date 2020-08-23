package com.qx.gulimall.elasticsearch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import javax.servlet.http.HttpServletRequest;

/**
 * @Classname HttpSessionConfig
 * @Description
 * @Date 2020/8/21 18:27
 * @Author 卿星
 */
@EnableRedisHttpSession
public class HttpSessionConfig {

    /**
     *  重写CookieSerializaer序列化，
     * @return
     */
    @Bean
    public CookieSerializer cookieSerializer(HttpServletRequest request){
        // 创建cookie默认序列化器并修改其中的配置
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        // 设置cookie作用路径
        cookieSerializer.setCookiePath("/");
        // 设置cookie名
        cookieSerializer.setCookieName("GULIMALL_SESSIONID");
        // 设置作用域，达到同父域下单点登录的效果 TODO 没有域名时，不能setDomainName
        cookieSerializer.setDomainName("gulimall.com");

        return cookieSerializer;

    }

    /**
     * 替换jdk默认序列化器
     * @return
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }

}
