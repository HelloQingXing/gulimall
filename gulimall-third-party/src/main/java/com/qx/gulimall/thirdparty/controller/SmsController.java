package com.qx.gulimall.thirdparty.controller;

import com.qx.common.utils.R;
import com.qx.gulimall.thirdparty.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Classname SmsController
 * @Description 短信
 * @Date 2020/8/18 19:20
 * @author  卿星
 */
@RestController
@RequestMapping("sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @GetMapping("send")
    public R sendSms(@RequestParam String phoneNum,@RequestParam String code){

        smsService.send(phoneNum,code);

        return R.ok();
    }


}
