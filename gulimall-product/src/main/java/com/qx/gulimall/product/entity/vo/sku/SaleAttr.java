package com.qx.gulimall.product.entity.vo.sku;

import lombok.Data;

import java.util.List;

/**
 * @Classname SaleAttr
 * @Description 销售属性
 * @Date 2020/8/16 17:26
 * @Created by 卿星
 */
@Data
public class SaleAttr {

    private Long attrId;
    private String attrName;
    private List<SkuItemVo.AttrValueWithSkuIds> valueWithSkuIds;
}
