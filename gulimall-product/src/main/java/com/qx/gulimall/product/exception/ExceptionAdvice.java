package com.qx.gulimall.product.exception;

import com.qx.common.exception.BizCodeEnum;
import com.qx.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.UnexpectedTypeException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 卿星
 * @Classname ExceptionAdvice
 * @Description 异常处理类
 * @Date 2020/7/12 16:28
 */
@Slf4j
// RestControllerAdvice = ControllerAdvice + ResponseBody
@RestControllerAdvice(basePackages = "com.qx.gulimall.product.controller")
public class ExceptionAdvice {

    // 标记该方法为异常处理方法 value：具体异常
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){
        // 记录日志
        log.error("数据不合法：" + e);
        // 获取结果集
        BindingResult bindingResult = e.getBindingResult();
        // 創建map封装结果
        Map<String,String> map = new HashMap<>();
        bindingResult.getFieldErrors().forEach((item) -> {
            // 将错误放入map
            map.put(item.getField(),item.getDefaultMessage());
        });
        // 使用自定义封装的结果集返回自定义内容
        return R.error(BizCodeEnum.VALID_EXCEPTION.getCode(),BizCodeEnum.VALID_EXCEPTION.getMsg()).put("data",map);
    }

    @ExceptionHandler(Exception.class)
    public R globalException(Exception e){

        log.error(e.getMessage());
        System.out.println(e);
        System.out.println(e.getMessage());
        throw new RuntimeException(e);
//        return R.error(BizCodeEnum.UNKNOW_EXEPTION.getCode(),BizCodeEnum.UNKNOW_EXEPTION.getMsg());
    }

}
