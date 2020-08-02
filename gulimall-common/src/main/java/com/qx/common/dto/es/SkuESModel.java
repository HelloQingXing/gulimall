package com.qx.common.dto.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/** @Classname SkuESModel @Description 商品页页面展示模型 @Date 2020/7/24 13:17 @Created by 卿星 */
@Data
public class SkuESModel {

  private Long skuId;
  private Long spuId;
  private String skuTitle;
  private String skuImg;
  private BigDecimal skuPrice;
  private Integer saleCount;
  private Boolean hasStock;
  private Double hotScore;
  private Long brandId;
  private Long catalogId;
  private String catalogName;
  private String brandName;
  private String brandImg;
  private List<Attr> attrs;

    @Data
    public static class Attr {

        private Long attrId;
        private String attrName;
        private String attrValue;
    }
}
