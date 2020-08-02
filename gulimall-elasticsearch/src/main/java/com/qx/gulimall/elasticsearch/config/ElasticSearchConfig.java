package com.qx.gulimall.elasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname ElasticSearchConfig
 * @Description Es配置类
 * @Date 2020/7/23 14:10
 * @Created by 卿星
 */
@Configuration
public class ElasticSearchConfig {

//    public static final RestClient COMMON_OPTIONS;

    @Bean
    public RestHighLevelClient getRestHighLevelClient(){

        // 创建连接
        HttpHost httpHost = new HttpHost("192.168.56.10", 9200, "http");
        // 构建启动类
        RestClientBuilder builder = RestClient.builder(httpHost);
        // 设置
//        COMMON_OPTIONS = builder.build();
        // 创建客服端
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(builder);

        return restHighLevelClient;
    }

}
