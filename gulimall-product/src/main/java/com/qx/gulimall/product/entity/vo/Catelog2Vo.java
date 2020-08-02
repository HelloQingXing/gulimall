package com.qx.gulimall.product.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Classname Catelog2Vo
 * @Description 二级分类&三级分类
 * @Date 2020/7/26 22:48
 * @Created by 卿星
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Catelog2Vo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 一级分类id
     */
    private String catalog1Id;
    /**
     * 三级分类集合
     */
    private List<Catelog3Vo> catalog3List;
    private String id;
    private String name;

    /**
     * 三级分类
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Catelog3Vo implements Serializable{

        private static final long serialVersionUID = 1L;

        /**
         * 二级分类id
         */
        private String catalog2Id;
        private String id;
        private String name;
    }

}
