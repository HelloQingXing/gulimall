package com.qx.gulimall.ware.feign;

import com.qx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Classname ProductFeignService
 * @Description 远程调用商品微服务
 * @Date 2020/7/21 14:16
 * @Created by 卿星
 */
@Service
@FeignClient("gulimall-product")
public interface ProductFeignService {

    @RequestMapping("/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);

}
