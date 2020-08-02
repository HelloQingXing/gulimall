package com.qx.gulimall.elasticsearch.vo;

import lombok.Data;

import java.util.List;

/**
 * @Classname SearchParam
 * @Description 检索参数，分装页面所有的查询条件
 * @Date 2020/8/1 0:21
 * @Created by 卿星
 */
@Data
public class SearchParam {
    /**
     * 关键字 -》skuTitle
     */
    private String keyword;

    /*排序*/
    /**
     * 排序条件，格式：sort= 销量 saleCount_asc/desc || 热度分 hostScore_asc/desc || 价格 skuPrice_asc/desc
     */
    private String sort;

    /*过滤*/
    /**
     * 是否有库存
     */
    private Boolean hasStock;


    /**
     * 商品ID
     */
    private List<Long> brandIds;
    /**
     * 价格 skuPrice=100_1000 / _500 / 500_
     */
    private String skuPrice;
    /**
     * 三级分类ID
     */
    private Long catlog3Id;
    /**
     * 属性集合 attr=1_8GB:6GB%2_5.5存
     */
    private List<String> attrs;

    /*分页参数*/

    /**
     * 页码
     */
    private Long pageNum;

}
