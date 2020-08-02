package com.qx.gulimall.product.feign;

import com.qx.common.dto.SkuMemberPriceDto;
import com.qx.common.dto.SkuReductionDto;
import com.qx.common.dto.SpuBoundDTo;
import com.qx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Classname CouponService
 * @Description feign接口 -- gulimall-coupon
 * @Date 2020/7/19 22:57
 * @Created by 卿星
 */
@FeignClient("gulimall-coupon")
@Component
public interface CouponService {

    @PostMapping("/coupon/memberprice/save/memberPriceDto/list")
    R saveMemberPriceDtoList(@RequestBody List<SkuMemberPriceDto> memberPriceDtoList);

    @PostMapping("/coupon/spubounds/save/spuBoundDTo")
    R saveSpuBoundDTo(@RequestBody SpuBoundDTo boundDTo);

    @PostMapping("/coupon/skufullreduction/save/sku/reduction/dto")
    R saveSkuReductionDto(@RequestBody SkuReductionDto skuReductionDto);
}
