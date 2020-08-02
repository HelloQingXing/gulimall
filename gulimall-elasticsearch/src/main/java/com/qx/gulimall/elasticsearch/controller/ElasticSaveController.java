package com.qx.gulimall.elasticsearch.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qx.common.dto.es.SkuESModel;
import com.qx.common.exception.BizCodeEnum;
import com.qx.common.utils.R;
import com.qx.gulimall.elasticsearch.config.ElasticSearchConfig;
import com.qx.gulimall.elasticsearch.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @Classname ESController
 * @Description ES
 * @Date 2020/7/24 16:54
 * @Created by 卿星
 */
@RestController
@RequestMapping("/es/save")
@Slf4j
public class ElasticSaveController {

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private ProductSaveService productSaveService;

    @PostMapping("/up/product/list")
    public R upProduct(@RequestBody List<SkuESModel> skuESModelList){

        // 上架商品
        Boolean flag = null;
        try {
            flag = productSaveService.upProduct(skuESModelList);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("SearchSaveController商品上架失败：{}",e);
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }

        if(flag){
            return R.ok();
        } else {
            return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }

    }

}
