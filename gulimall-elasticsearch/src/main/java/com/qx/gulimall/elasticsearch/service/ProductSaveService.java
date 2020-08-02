package com.qx.gulimall.elasticsearch.service;

import com.qx.common.dto.es.SkuESModel;

import java.io.IOException;
import java.util.List;

/**
 * @Classname ProductSaveService
 * @Description 商品保存
 * @Date 2020/7/25 10:06
 * @Created by 卿星
 */
public interface ProductSaveService {

    Boolean upProduct(List<SkuESModel> skuESModelList) throws IOException;
}
