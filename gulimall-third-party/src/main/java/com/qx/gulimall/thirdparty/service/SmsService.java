package com.qx.gulimall.thirdparty.service;

/**
 * @Classname SmsService
 * @Description 短信
 * @Date 2020/8/18 19:20
 * @Created by 卿星
 */
public interface SmsService {
    /**
     * 发送短信
     * @param phoneNum
     * @param code
     */
    void send(String phoneNum, String code);
}
