package com.qx.common.dto;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Classname SkuMemberPriceDto 商品会员价格传输对象
 * @Date 2020/7/19 23:39 @Created by 卿星
 * */
@Data
public class SkuMemberPriceDto {

    /**
     * id
     */
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * 会员等级id
     */
    private Long memberLevelId;
    /**
     * 会员等级名
     */
    private String memberLevelName;
    /**
     * 会员对应价格
     */
    private BigDecimal memberPrice;
    /**
     * 可否叠加其他优惠[0-不可叠加优惠，1-可叠加]
     */
    private Integer addOther;

}
