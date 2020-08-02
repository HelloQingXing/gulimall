package com.qx.gulimall.product.feign;

import com.qx.common.dto.StockVo;
import com.qx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Classname WareFeignService
 * @Description ware微服务远程调用模块
 * @Date 2020/7/24 16:32
 * @Created by 卿星
 */
@Service
@FeignClient(value = "gulimall-ware")
public interface WareFeignService {

    @GetMapping("/ware/waresku/has-stoke/{skuId}")
    R hasStock(@PathVariable Long skuId);
    @GetMapping("/ware/waresku/has-stoke")
    R hasStockBySkuIds(@RequestBody List<Long> skuIdList);
}
