package com.qx.common.constant;

/**
 * @Classname PurchaseStatusConstant
 * @Description 采购状态
 * @Date 2020/7/20 22:51
 * @Created by 卿星
 */
public class WareConstant {

    public enum PurchaseStatusEnum{

        CREATED(0,"新建"),
        ASSIGN(1,"已分配"),
        RECEIVED(2,"已领取"),
        FINISH(3,"已完成"),
        HASERROR(4,"有异常");

        private Integer code;
        private String msg;

        private PurchaseStatusEnum(Integer code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }


    public enum PurchaseDetailStatusEnum{

        CREATED(0,"新建"),
        ASSIGN(1,"已分配"),
        BUYING(2,"正在采购"),
        FINISH(3,"已完成"),
        HASERROR(4,"采购失败");

        private Integer code;
        private String msg;

        private PurchaseDetailStatusEnum(Integer code,String msg){
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

}
