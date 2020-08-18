package com.qx.gulimall.auth.controller;

import com.qx.common.exception.BizCodeEnum;
import com.qx.common.utils.R;
import com.qx.gulimall.auth.feign.ThirdPartyFeignService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Classname SignController
 * @Description 登录和注册及子功能
 * @Date 2020/8/18 21:25
 * @Author 卿星
 */
@RestController
@RequestMapping("sign")
public class SignController {

    @Autowired
    ThirdPartyFeignService thirdPartyFeignService;
    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping("sendSms")
    public R sendSms(@RequestParam String phoneNum){

        // TODO 接口防刷

        // 发送验证码前先从redis中尝试是否能够获取 key -> sms:code:phoneNum code
        String codeFromCache = redisTemplate.opsForValue().get("sms:code:" + phoneNum);
        if(!StringUtils.isEmpty(codeFromCache)){
//            String codeStr = codeFromCache.split("_")[1];
            return R.error(BizCodeEnum.SMS_FREQUENCY_FAST_EXCEPTION.getCode(),BizCodeEnum.SMS_FREQUENCY_FAST_EXCEPTION.getMsg());
        }

        // UUID生成随机验证码
        String code = UUID.randomUUID().toString().substring(0, 4);
        // 远程调用发送
        thirdPartyFeignService.sendSms(phoneNum,code);
        // 保存到redis中
        redisTemplate.opsForValue().set("sms:code:" + phoneNum,code,60, TimeUnit.SECONDS);

        return R.ok();
    }
}
