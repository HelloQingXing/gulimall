package com.qx.common.exception;

/**
 * @Classname BizCodeEnum
 * @Description 异常返回枚举
 * @Date 2020/7/12 17:40
 * @Created by 卿星
 */
/***
 * 错误码和错误信息定义类
 * 1. 错误码定义规则为5为数字
 * 2. 前两位表示业务场景，最后三位表示错误码。例如：100001。10:通用 001:系统未知异常
 * 3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
 * 错误码列表：
 *  10: 通用
 *      001：参数格式校验
 *      002：短信验证码发送频率过快，60秒内只能发送一次
 *  11: 商品
 *  12: 订单
 *  13: 购物车
 *  14: 物流
 */
public enum  BizCodeEnum {

    UNKNOW_EXEPTION(10000,"未知异常"),
    VALID_EXCEPTION(10001,"参数格式效验失败"),
    SMS_FREQUENCY_FAST_EXCEPTION(10002,"短信验证码发送频率过快，60秒内只能发送一次"),
    PRODUCT_UP_EXCEPTION(11000,"上架失败"),
    ;

    // 状态码
    private Integer code;
    // 消息
    private String msg;

    // 枚举构造方法默认私有
    BizCodeEnum(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public Integer getCode() {
        return code;
    }
}
