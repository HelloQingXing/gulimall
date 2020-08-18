package com.qx.gulimall.auth.feign;

import com.qx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Classname ThirdPartyFeignService
 * @Description 第三方微服务
 * @Date 2020/8/18 21:29
 * @Author 卿星
 */
@FeignClient("gulimall-third-party")
@Service
public interface ThirdPartyFeignService {

    @GetMapping("sms/send")
    R sendSms(@RequestParam String phoneNum, @RequestParam String code);
}
