package com.qx.gulimall.elasticsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.qx.common.dto.es.SkuESModel;
import com.qx.gulimall.elasticsearch.constant.EsConstant;
import com.qx.gulimall.elasticsearch.service.MallSearchService;
import com.qx.gulimall.elasticsearch.vo.SearchParam;
import com.qx.gulimall.elasticsearch.vo.SearchResult;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Classname MallSearchServiceImpl
 * @Description 商城检索
 * @Date 2020/8/1 10:39
 * @Created by 卿星
 */
@Service
public class MallSearchServiceImpl implements MallSearchService  {

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Override
    public SearchResult searchByParam(SearchParam searchParam) throws IOException {

        // 构建请求对象
        SearchRequest request = createSearchRequestByParam(searchParam);


        SearchResult result = getResultBySearchRequest(request);


        return null;
    }

    private SearchRequest createSearchRequestByParam(SearchParam searchParam) {
        // 构建请求对象
        SearchRequest request = new SearchRequest(EsConstant.PRODUCT_INDEX);
        // 设置请求参数
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 多条件查询
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        // 模糊匹配-关键字
        if(!StringUtils.isEmpty(searchParam.getKeyword())){
            queryBuilder.must(QueryBuilders.termQuery("skuTitle",searchParam.getKeyword()));
        }
        // 过滤条件
        // 过滤-term字段
        // 三級id
        if(!StringUtils.isEmpty(searchParam.getCatlog3Id())){
            queryBuilder.filter(QueryBuilders.termQuery("catalogId",searchParam.getCatlog3Id()));
        }
        // 库存
        if(searchParam.getHasStock()){
            queryBuilder.filter(QueryBuilders.termQuery("hasStock",searchParam.getHasStock()));
        }
        // 商品id
        if(searchParam.getBrandIds() != null && searchParam.getBrandIds().size() > 0){
            queryBuilder.filter(QueryBuilders.termsQuery("brandId",searchParam.getBrandIds()));
        }
        // 过滤-nested嵌套查询 attrs=1_8GB:6GB&attrs=2_5.5存
        if(searchParam.getAttrs() != null && searchParam.getAttrs().size() > 0){

            // 遍历设置属性
            List<String> attrs = searchParam.getAttrs();
            for (String attr : attrs) {
                // 分隔属性
                String[] s = attr.split("_");
                String attrId = s[0];
                String attrVals = s[1];
                QueryBuilder builder = null;
                // 判断是否有多个属性值
                if(attrVals.contains(":")){
                    String[] split = attrVals.split(":");
                    // 组装参数
                    builder = QueryBuilders.boolQuery()
                            .must(QueryBuilders.termsQuery("attrs.attrId", attrId))
                            .must(QueryBuilders.termsQuery("attrs.attrValue", split));
                } else {
                    // 组装参数
                    builder = QueryBuilders.boolQuery().
                            must(QueryBuilders.termsQuery("attrs.attrId",attrId)).
                            must(QueryBuilders.termsQuery("attrs.attrValue",attrVals));
                }
                // 设置条件
                QueryBuilder nestedBuilder = QueryBuilders.nestedQuery("attrs", builder, ScoreMode.None);
                // 将该条件加入主条件中
                queryBuilder.filter(nestedBuilder);
            }
        }

        // 过滤-range价格范围 skuPrice=100_1000 / _500 / 500_
        String skuPrice = searchParam.getSkuPrice();
        if(!StringUtils.isEmpty(skuPrice)){
            // 分隔价格
            String[] s = skuPrice.split("_");
            RangeQueryBuilder builder = QueryBuilders.rangeQuery("skuPrice");
            // 判断属于哪一种情况
            if(s.length == 2){
                builder.gte(s[0]).lte(s[1]);
            }else if(skuPrice.startsWith("_")){
                builder.lte(s[0]);
            } else if(skuPrice.endsWith("_")){
                builder.gte(s[0]);
            }
            queryBuilder.filter(builder);
        }
        // 高亮显示
        sourceBuilder.highlighter().field("skuTitle").preTags("<b style='color=red'>").postTags("</b>");

        // 聚合
        sourceBuilder.aggregation(AggregationBuilders
                .nested("attr-agg","attrs")
                .subAggregation(AggregationBuilders.terms("id-agg").field("attrs.attrId").size(10)))
                .aggregation(AggregationBuilders.terms("name-agg").field("attrs.attrName").size(10))
                .aggregation(AggregationBuilders.terms("value-agg").field("attrs.attrValue").size(10));

        request.source(sourceBuilder);

        return request;
    }


    private SearchResult getResultBySearchRequest(SearchRequest request) throws IOException {

        // 获取响应
        SearchResponse response = restHighLevelClient.search(request,RequestOptions.DEFAULT);
    // 分析响应结果，组装数据
        SearchResult result = new SearchResult();
        List<SkuESModel> products = new ArrayList<SkuESModel>();
        // 分析命中结果
        for (SearchHit hit : response.getHits().getHits()) {
            // 得到source
            String sourceAsString = hit.getSourceAsString();
            // 转换数据
            SkuESModel skuESModel = JSON.parseObject(sourceAsString, SkuESModel.class);
            // 添加数据
            products.add(skuESModel);
        }
        // 分析聚合元素
        Map<String, Aggregation> aggregationMap = response.getAggregations().asMap();

        // 组装集合数据
        Aggregation aggregation = aggregationMap.get("name-agg");

        Map<String, Object> metaData = aggregation.getMetaData();
        Object o = metaData.get("name-agg");

        return null;
    }

}
