package com.qx.gulimall.elasticsearch.vo;

import com.qx.common.dto.es.SkuESModel;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Classname SearchResult
 * @Description 检索结果
 * @Date 2020/8/1 11:28
 * @Created by 卿星
 */
@Data
public class SearchResult {

    /**页面所有商品数据*/
    private List<SkuESModel> products;

    /**页面所有品牌*/
    private List<Brand> brands;

    /**
     * 页面所有属性数据
     * */
    private List<Attr> attrs;

    /**
     * 页面展示所有分类信息
     */
    private List<Catelog> categorys;
    /**
     * 面包屑——导航条
     */
    private List<NavVo> navVos = new ArrayList<>();

    /**
     * 分页数据
     */
    /**
     * 当前页，默认第一页
     */
    private Integer current = 1;
    /**
     * 总记录数
     */
    private Integer total;
    /**
     * 总页数
     */
    private Integer totalPage;
    /*
    * 页码
    * */
    private  List<Integer> pageNums;

    /*NavVo 面包屑 -- 导航条*/
    @Data
    public static class NavVo{
        private String NavName;
        private String NavValue;
        /**
         * 清除该面包屑后跳转地址
         */
        private String link;
    }

    @Data
    public static class Brand {
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class Catelog {
        private Long catalogId;
        private String catalogName;
    }


    @Data
    public static class Attr {

        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

}
