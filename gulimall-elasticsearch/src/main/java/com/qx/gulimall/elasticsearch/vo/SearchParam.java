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
     * 默认升序
     * 排序条件，格式：sort= 销量 saleCount_asc/desc || 热度分 hostScore_asc/desc || 价格 skuPrice_asc/desc
     */
    private String sort;

    /*过滤*/
    /**
     * 是否有库存，默认有库存
     */
    private Boolean hasStock = true;


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
    private Integer catlog3Id;
    /**
     * 属性集合 格式：attrs=attrId+"_"+属性值   attrs=1_8GB:6GB&attrs=2_5.5存
     */
    private List<String> attrs;

    /*分页参数*/

    /**
     * 页码:默认是1
     */
    private Integer pageNum = 1;

}
