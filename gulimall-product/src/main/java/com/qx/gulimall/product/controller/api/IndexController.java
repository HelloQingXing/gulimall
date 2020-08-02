package com.qx.gulimall.product.controller.api;

import com.qx.gulimall.product.entity.CategoryEntity;
import com.qx.gulimall.product.entity.vo.Catelog2Vo;
import com.qx.gulimall.product.service.CategoryService;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Classname IndexController
 * @Description 首页
 * @Date 2020/7/26 14:12
 * @Created by 卿星
 */
@Controller
public class IndexController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedissonClient redissonClient;

    @GetMapping(value = {"/","index.html"})
    public ModelAndView index(){
        ModelAndView mv = new ModelAndView("index");
        // 获取数据
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Catlog();
        // 将数据添加到视图中
        mv.addObject("categorys",categoryEntities);
//        System.out.println("categoryEntities \n" + mv.getModel().get("categorys"));
        // 返回页面
        return mv;
    }

    @ResponseBody
    @GetMapping("index/json/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatelogWithOrder(){

        Map<String, List<Catelog2Vo>> map = categoryService.getCatelogWithOrder();
//        System.out.println(map);
        return map;
    }


    @ResponseBody
    @GetMapping("hello")
    public String hello(){

        RLock lock = redissonClient.getLock("lock");

        try {
            // 传参时：如果未在指定时间内解锁，则不回执行看萌狗续期操作
//            lock.lock(5, TimeUnit.SECONDS);
            // 如果程序未执行完，不会删除当前锁，看萌狗会一直定时续期知道30秒结束
            lock.lock();
            System.out.println("执行线程" + Thread.currentThread().getId());

            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return "hello";
    }

    /**
     * 加锁：保证能读到最新数据，加锁期间，读锁是一个排它锁（互斥锁，独享锁），读锁是一个共享锁
     * 写锁没释放就必须等待
     * 读+读：相当于无锁，并发读
     * 读+写：等待写锁释放
     * 写+读：等待写锁释放
     * 写+写：阻塞方式
     * 只要有写，都必须等待
     * @return
     */
    @ResponseBody
    @GetMapping("write")
    public String write(){

        RLock lock = redissonClient.getReadWriteLock("lock").writeLock();

        try {
            // 传参时：如果未在指定时间内解锁，则不回执行看萌狗续期操作
//            lock.lock(5, TimeUnit.SECONDS);
            // 如果程序未执行完，不会删除当前锁，看萌狗会一直定时续期知道30秒结束
            lock.lock();
            System.out.println("执行线程" + Thread.currentThread().getId());

            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return "write";
    }

    @ResponseBody
    @GetMapping("read")
    public String read(){

        RLock lock = redissonClient.getReadWriteLock("lock").readLock();

        try {
            // 传参时：如果未在指定时间内解锁，则不回执行看萌狗续期操作
//            lock.lock(5, TimeUnit.SECONDS);
            // 如果程序未执行完，不会删除当前锁，看萌狗会一直定时续期知道30秒结束
            lock.lock();
            System.out.println("执行线程" + Thread.currentThread().getId());

            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return "read";
    }

    @ResponseBody
    @GetMapping("create/lock")
    public String closeDoor(){

        // 获取锁
        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        // 设置位置
        boolean b = true;
        try{
            // ，一个"door"锁只能创建一次，不能重复创建
            b = door.trySetCount(5);
            Thread.sleep(5000);
        } finally{
            return b == true ? "锁创建成功" : "创建失败";
        }

    }

    @GetMapping("release/lock")
    @ResponseBody
    public String openDoor(){

        RCountDownLatch door = redissonClient.getCountDownLatch("door");
        try{
            // 获取位置
            door.countDown();
            Thread.sleep(5000);
        } finally{
            return "获取锁成功" + door.getCount();
        }
    }

    /**
     * 可以利用信号灯来执行限流，只有在规定流量范围内，才能进入程序
     * semaphore：信号灯
     * @return
     */
    @GetMapping("semphore/acquire")
    @ResponseBody
    public String trySignal() throws InterruptedException {
        // 获取操作对象
        RSemaphore rSemaphore = redissonClient.getSemaphore("signal1");
        // 设置该“signal”剩余位置
//        rSemaphore.trySetPermits(10);
        // 请求获取位置
        rSemaphore.acquire();

        Thread.sleep(3000);

        return  rSemaphore.getName() + "< -- >signal acquire";
    }

    @GetMapping("semphore/release")
    @ResponseBody
    public String releaseSignal() throws InterruptedException {

        RSemaphore rSemaphore = redissonClient.getSemaphore("signal1");
//        rSemaphore.release(5);
        // 释放一个位置
        rSemaphore.release();

        Thread.sleep(3000);

        return  rSemaphore.getName() + "< -- >signal 释放成功";
    }

}
