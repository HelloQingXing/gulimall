package com.qx.gulimall.auth.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Classname WebInfoVo
 * @Description
 * @Date 2020/8/21 11:49
 * @Author 卿星
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class WebInfoVo {
    /**
     * 微博唯一标识
     */
    private String uid;
    /**
     * 用户名
     */
    private String name;
    /**
     * 头像
     */
    private String avator;
     /**
     * 头像
     */
    private String gender;

}
