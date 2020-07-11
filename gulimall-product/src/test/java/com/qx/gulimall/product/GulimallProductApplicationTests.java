package com.qx.gulimall.product;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.UploadFileRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;

@SpringBootTest
class GulimallProductApplicationTests {

  @Autowired
  OSSClient ossClient;

  @Test
  void contextLoads() throws Throwable {

    ossClient.putObject(
        "gulimall-qx", "0d40c24b264aa511.jpg", new FileInputStream("D:\\360安全浏览器下载\\尚硅谷\\谷粒商城\\资料源码\\pics\\0d40c24b264aa511.jpg"));
    System.out.println("上传成功");
  }
}
