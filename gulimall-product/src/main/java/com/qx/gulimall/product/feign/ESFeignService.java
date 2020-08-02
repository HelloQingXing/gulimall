package com.qx.gulimall.product.feign;

import com.qx.common.dto.es.SkuESModel;
import com.qx.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Classname ESFeignService
 * @Description ES微服务
 * @Date 2020/7/24 16:50
 * @Created by 卿星
 */
@Service
@FeignClient("gulimall-elasticsearch")
public interface ESFeignService {

    @PostMapping("/es/save/up/product/list")
    R upProduct(@RequestBody List<SkuESModel> skuESModelList);
}
