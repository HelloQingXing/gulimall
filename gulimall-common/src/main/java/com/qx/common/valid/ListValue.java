package com.qx.common.valid;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @Classname ListValue
 * @Description 自定义校验注解
 * @Date 2020/7/12 22:49
 * @author 卿星
 */
@Documented
// 指定自定义的校验注解
@Constraint(validatedBy = { ListValueConstraintValidator.class })
// 该注解可以作用的地方
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
// 校验时机
@Retention(RUNTIME)
public @interface ListValue {

    // 自定义消息接收路径
    String message() default "{com.qx.common.valid.ListValue.message}";

    // 分组
    Class<?>[] groups() default { };

    //
    Class<? extends Payload>[] payload() default { };

    // 自定义数组值
    int[] value() default { };


}
