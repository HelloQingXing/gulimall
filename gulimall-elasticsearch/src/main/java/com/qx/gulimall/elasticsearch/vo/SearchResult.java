package com.qx.gulimall.elasticsearch.vo;

import com.qx.common.dto.es.SkuESModel;
import lombok.Data;

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
     * 分页数据
     */
    /**
     * 挡墙页
     */
    private Long current;
    /**
     * 总页数
     */
    private Long total;
    /**
     * 每页显示数
     */
    private Long size;


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
        private String attrValue;
    }

}
