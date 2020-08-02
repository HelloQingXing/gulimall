package com.qx.common.valid;

import com.google.errorprone.annotations.Var;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author 卿星
 * @Classname ListValueConstraintValidator
 * @Description 校验器
 * @Date 2020/7/12 23:04
 * ListValue：枚举注解类
 * Integer：支持类型
 * ConstraintValidator<ListValue,Integer>
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {

    // 初始化一个set
    Set<Integer> set = new HashSet<>();

    /**
     * Initializes the validator in preparation for
     *  calls.
     * The constraint annotation for a given constraint declaration
     * is passed.
     * <p>
     * This method is guaranteed to be called before any use of this instance for
     * validation.
     * <p>
     * The default implementation is a no-op.
     *
     * @param constraintAnnotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {

        int[] value = constraintAnnotation.value();
        for (int i : value) {
            // 添加到set中
            set.add(i);
        }
    }

    /**
     * Implements the validation logic.
     * The state of {@code value} must not be altered.
     * <p>
     * This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     *
     * @param value   object to validate 要判断的值
     * @param context context in which the constraint is evaluated
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return set.contains(value);
    }
}
