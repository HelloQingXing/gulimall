package com.qx.gulimall.auth.entity.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @Classname RegisterVo
 * @Description
 * @Date 2020/8/19 10:57
 * @Author 卿星
 */
@Data
public class MemberRegisterVo {

//    @NotBlank(message = "用户名不能为空") 如果使用了length限定了长度，如果用户名为空也能检测出来
    @Length(min = 4,max = 20,message = "用户名不能为空，且用户名长度必须在4~20个字符以内")
    private String username;
    //    @NotBlank(message = "密码不能为空")
    @Length(min = 6,max = 20,message = "密码不能为空，且密码长度必须在6~20个字符以内")
    private String password;
    @NotNull
    @Pattern(regexp = "^1[356789]{1}\\d{9}$",message = "手机号格式不正确")
    private String phoneNum;
    @NotBlank(message = "验证码不能为空")
    private String code;
}
