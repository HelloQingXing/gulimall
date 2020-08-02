package com.qx.common.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Classname SkuReductionDto
 * @Description 优惠信息
 * @Date 2020/7/20 15:58
 * @Created by 卿星
 */
@Data
public class SkuReductionDto {

    private Long skuId;
    private int fullCount;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;

}
