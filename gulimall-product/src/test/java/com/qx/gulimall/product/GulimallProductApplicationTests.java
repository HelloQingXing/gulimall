package com.qx.gulimall.product;

//import com.aliyun.oss.OSSClient;
//import com.aliyun.oss.model.UploadFileRequest;
import com.qx.gulimall.product.service.AttrService;
import com.qx.gulimall.product.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class GulimallProductApplicationTests {

//  @Autowired
//  OSSClient ossClient;

  @Autowired
  private CategoryService categoryService;
  @Autowired
  private AttrService attrService;
  @Autowired
  private RedissonClient redissonClient;

  @Test
  void contextLoads() throws Throwable {

//    ossClient.putObject(
//        "gulimall-qx", "0d40c24b264aa511.jpg", new FileInputStream("D:\\360安全浏览器下载\\尚硅谷\\谷粒商城\\资料源码\\pics\\0d40c24b264aa511.jpg"));
//    System.out.println("上传成功");
  }

  @Test
  void test(){
    Long[] catlogPath = categoryService.getCatlogPath(165L);
    System.out.println(Arrays.asList(catlogPath));
  }

  @Test
  void test2(){
    HashMap<String, Object> objectObjectHashMap = new HashMap<>();
    objectObjectHashMap.put("page","1");
    objectObjectHashMap.put("limit","10");
    objectObjectHashMap.put("key","1");
    attrService.queryBasePage(objectObjectHashMap,225L);
  }

  @Test
  void redissonTest(){
    // java.lang.IllegalArgumentException: Redis url should start with redis:// or rediss:// (for SSL connection)
    System.out.println(redissonClient);
  }

  @Test
  public void redissonSimpleOperation() throws InterruptedException {

    for (int i = 0; i < 20; i++) {
      new Thread(
          () -> {

            RLock lock = redissonClient.getLock("lock");

            try {
              lock.lock(30, TimeUnit.SECONDS);
              System.out.println("执行线程" + Thread.currentThread().getId());
              Thread.sleep(10000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            } finally {
//              lock.unlock();
            }
          });
    }



  }
}
