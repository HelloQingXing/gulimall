package com.qx.gulimall.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    public CookieSerializer cookieSerializer(){

        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        // 设置cookie作用路径
        cookieSerializer.setCookiePath("/");
        // 设置cookie名
        cookieSerializer.setCookieName("GULIMALL_SESSIONID");
        // 设置作用域，达到同父域下单点登录效果 TODO 没有域名时，不能setDomainName
        cookieSerializer.setDomainName("gulimall.com");

        return cookieSerializer;

    }

    /**
     * 替换jdk默认序列化器
     * bean的名字必须为 springSessionDefaultRedisSerializer，其他名字不行
     * @return
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }

}
