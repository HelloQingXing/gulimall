package com.qx.gulimall.product.controller.api;

import com.qx.gulimall.product.entity.vo.sku.SkuItemVo;
import com.qx.gulimall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Classname ItemController
 * @Description 商品详情
 * @Date 2020/8/13 23:03
 * @Created by 卿星
 */
@Controller
public class ItemController {

    @Autowired
    private SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String item(@PathVariable Long skuId, Model model){

        SkuItemVo skuItemVo = skuInfoService.itemPageBySkuId(skuId);

        model.addAttribute("sku",skuItemVo);

        return "item";
    }

}
