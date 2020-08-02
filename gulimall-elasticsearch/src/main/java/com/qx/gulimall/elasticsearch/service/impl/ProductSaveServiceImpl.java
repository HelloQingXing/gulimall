package com.qx.gulimall.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.qx.common.dto.es.SkuESModel;
import com.qx.gulimall.elasticsearch.constant.EsConstant;
import com.qx.gulimall.elasticsearch.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Classname ProductSaveServiceImpl
 * @Description 商品上架
 * @Date 2020/7/25 10:07
 * @Created by 卿星
 */
@Slf4j
@Service
public class ProductSaveServiceImpl implements ProductSaveService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean upProduct(List<SkuESModel> skuESModelList) throws IOException {

        // 创建BulkRequest对象
        BulkRequest bulkRequest = new BulkRequest();

        // 遍历
        for (SkuESModel skuESModel : skuESModelList) {
            // 将数据转为json
            String s = JSON.toJSONString(skuESModel);
            // 创建索引
            IndexRequest indexRequest = new IndexRequest(EsConstant.PRODUCT_INDEX);
            // 设置ID标识
            indexRequest.id(skuESModel.getSkuId().toString());
            // 设置数据
            indexRequest.source(s, XContentType.JSON);
            // 将其添加到bulk桶中
            bulkRequest.add(indexRequest);
        }
        // 执行保存
        BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        List<String> idList = Arrays.stream(response.getItems()).map(BulkItemResponse::getId).collect(Collectors.toList());

        if(response.hasFailures()){
            log.error("ES上传失败：{}" + idList);
            return false;
        }

        return true;
    }
}
