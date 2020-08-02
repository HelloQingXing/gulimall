package com.qx.gulimall.thirdparty.controller;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.qx.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Classname OssController
 * @Description 对象存储
 * @Date 2020/7/11 16:43
 * @Created by 卿星
 */
@RequestMapping("/thirdparty/oss")
@RestController
public class OssController {

    @Autowired
    private OSS ossClient;

    @Value("${spring.cloud.alicloud.access-key}")
    private String accessKey;
    @Value("${spring.cloud.alicloud.secret-key}")
    private String secretKey;
    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endpointKey;
    @Value("${spring.cloud.alicloud.oss.bucketName}")
    private String bucketName;

    @RequestMapping("policy")
    public R getPoliceoGet() {

        String accessId = accessKey; // 请填写您的AccessKeyId。
        String accessKey = secretKey; // 请填写您的AccessKeySecret。
        String endpoint = endpointKey; // 请填写您的 endpoint。
        String bucket = bucketName; // 请填写您的 bucketname 。
        String host = "https://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint
        // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
//        String callbackUrl = "http://88.88.88.88:8888";
        // 定义文件夹名称
        String dateTime = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String dir = dateTime + "/"; // 用户上传文件时指定的前缀。

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessId, accessKey);

        Map<String, String> respMap = null;
        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));


        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            System.out.println(e.getMessage());
        } finally {
            ossClient.shutdown();
        }

        return R.ok().put("data",respMap);
    }


    @DeleteMapping("remove/{}")
    public R remove(String key){

        ossClient.deleteObject(bucketName,"270b869a-bab2-44e7-87fc-376b08632766_02.jpg");
        return R.ok();
    }

}
