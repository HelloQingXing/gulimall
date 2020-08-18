package com.qx.gulimall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Classname GuliMallWebConfig
 * @Description 谷粒商城页面跳转配置
 * @Date 2020/8/17 17:19
 * @Created by 卿星
 */
@Configuration
public class GuliMallWebConfig implements WebMvcConfigurer {

    /**
     * 无需执行逻辑的直接跳转
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        // 登录
        registry
                // 设置解析的地址
                .addViewController("login.html")
                // 设置跳转页面
                .setViewName("login");
        // 注册
        registry.addViewController("register.html").setViewName("register");
    }
}
