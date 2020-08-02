package com.qx.common.constant;

/**
 * @Classname ProductConstant
 * @Description 商品常量
 * @Date 2020/7/16 14:00
 * @Created by 卿星
 */
public class ProductConstant {

    public enum AttrEnum{

        ATTR_TYPE_BASE(1,"基本属性"),ATTR_TYPE_SALE(0,"销售属性");

        private Integer code;
        private String msg;

        private AttrEnum(Integer code,String msg){
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

    public enum StatusEnum{

        SPU_UP(1,"商品上架"),SPU_DOWN(0,"商品下架"),SPU_NEW(2,"商品新建");

        private Integer code;
        private String msg;

        private StatusEnum(Integer code,String msg){
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
