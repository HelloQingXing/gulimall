package com.qx.gulimall.auth.utill;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Classname WeiboRequestParam
 * @Description
 * @Date 2020/8/20 18:54
 * @Author 卿星
 */
@ConfigurationProperties("weibo.login.param")
@Component
@Data
public class WeiboRequestParam {

    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
