package com.qx.gulimall.product.entity.vo.sku;

import com.alibaba.fastjson.JSON;
import com.qx.gulimall.product.entity.SkuImagesEntity;
import com.qx.gulimall.product.entity.SkuInfoEntity;
import com.qx.gulimall.product.entity.SpuInfoDescEntity;
import com.qx.gulimall.product.entity.SpuInfoEntity;
import com.qx.gulimall.product.entity.vo.spu.Attr;
import lombok.Data;

import java.util.Comparator;
import java.util.List;

/**
 * @Classname SkuItemVo
 * @Description sku
 * @Date 2020/8/14 11:36
 * @Created by 卿星
 */
@Data
public class SkuItemVo {

    /**
     * sku基本信息
     */
    private SkuInfoEntity skuInfo;
    /**
     * 销售属性（sku）
     */
    private List<SaleAttr> saleAttrs;
    /**
     * 基本属性 - spu规格参数
     */
    private List<AttrGroupVo> baseAttrs;
    /**
     * sku图片
     */
    private List<SkuImagesEntity> skuImages;

    /**
     * spu介绍
     */
    private SpuInfoDescEntity spuInfoDesc;

    /**
     * 是否有货 ,默认有
     */
    private Boolean hasStock = true;

    @Data
    public static class SaleAttr {
        private Long attrId;
        private String attrName;
        private List<AttrValueWithSkuIds> valueWithSkuIds;
    }

    @Data
    public static class AttrGroupVo{
        private String attrGroupName;
        private List<Attr> attrs;
    }

    @Data
    public static class AttrValueWithSkuIds{
        private String attrValue;
        private String skuIds;
    }

}
