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
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.beans.Encoder;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

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
    public SearchResult searchByParam(SearchParam searchParam, HttpServletRequest request) {

        // 构建请求对象
        SearchRequest searchRequest = createSearchRequestByParam(searchParam);

        // 封装请求结果
        SearchResult result = null;
        try {
            result = getResultBySearchRequest(searchRequest,request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(result);

        return result;
    }

    /**
     * 组装请求体
     * 模糊匹配、过滤（分类、属性、品牌、库存、价格）、排序、高亮、分页、聚合
     * @param searchParam：请求参数
     * @return
     */
    private SearchRequest createSearchRequestByParam(SearchParam searchParam) {
        // 创建请求对象，指定索引
        SearchRequest request = new SearchRequest(EsConstant.PRODUCT_INDEX);
        // 设置搜索源
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // 构建请求体
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        // 模糊匹配-关键字匹配（参与评分）
        String keyword = searchParam.getKeyword();
        if(!StringUtils.isEmpty(keyword)){
            queryBuilder.must(QueryBuilders.termQuery("skuTitle",keyword));
        }

        // 过滤-三级分类id
        Integer catlog3Id = searchParam.getCatlog3Id();
        if(catlog3Id != null){
            queryBuilder.filter(QueryBuilders.termQuery("catalogId",catlog3Id));
        }
        // 过滤—库存(默认为true)
        Boolean hasStock = searchParam.getHasStock();
        queryBuilder.filter(QueryBuilders.termQuery("hasStock",hasStock));
        // 过滤-商品id
        List<Long> brandIds = searchParam.getBrandIds();
        if(brandIds != null && brandIds.size() > 0){
            queryBuilder.filter(QueryBuilders.termsQuery("brandId",brandIds));
        }
        // 过滤-属性
        // 因为是级联属性，会扁平化处理，故必须嵌套查询 attrs=1_8GB:6GB&attrs=2_5.5存

        List<String> attrs = searchParam.getAttrs();
        if(attrs != null && attrs.size() > 0){
            BoolQueryBuilder boolAttrQuery = QueryBuilders.boolQuery();
            // 分隔属性
            for (String attr : attrs) {
                String[] s = attr.split("_");
                String attrId = s[0];
                String attrVals = s[1];
                // 一个属性名对应属性值可能有多个 8GB:6GB
                if(attrVals.contains(":")){
                    String[] split = attrVals.split(":");
                    List<String> collectAttrs = Arrays.stream(split).collect(Collectors.toList());

                    boolAttrQuery.must(QueryBuilders.termsQuery("attrs.attrValue",collectAttrs));
                } else {
                    boolAttrQuery.must(QueryBuilders.termQuery("attrs.attrValue",attrVals));
                }
                // 设置attrId
                boolAttrQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
            }
            // 将boolQuery添加到queryBuilder、嵌套查询
            NestedQueryBuilder attrs1 = QueryBuilders.nestedQuery("attrs", boolAttrQuery, ScoreMode.None);
            queryBuilder.filter(attrs1);
        }


        // 过滤--按照价格范围 skuPrice=100_1000 / _500 / 500_
        String skuPrice = searchParam.getSkuPrice();
        if(!StringUtils.isEmpty(skuPrice)){
            RangeQueryBuilder skuPriceBuilder = QueryBuilders.rangeQuery("skuPrice");
            // 如果以 _ 开头，则表示小于
            if(skuPrice.startsWith("_")){
                skuPriceBuilder.lte(skuPrice.split("_")[0]);
            } else if(skuPrice.endsWith("_")){
                // 如果以 _ 结尾，则表示大于
                skuPriceBuilder.gte(skuPrice.split("_")[0]);
            } else {
                // 否则为其范围
                String[] s = skuPrice.split("_");
                String low = s[0];
                String high = s[1];
                skuPriceBuilder.gte(low);
                skuPriceBuilder.lte(high);
            }
            // 设置到查询条件中
            queryBuilder.filter(skuPriceBuilder);
        }

        // 将查询条件放入源中
        searchSourceBuilder.query(queryBuilder);

        // 高亮-关键字
        String keywordLight = searchParam.getKeyword();
        if(!StringUtils.isEmpty(keywordLight)){
            // 通过HighlightBuilder构建高亮代码条件
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder
                    // 设置字段
                    .field("skuTitle")
                    // 设置前置代码
                    .preTags("<b style='color: red;'>")
                    // 后置
                    .postTags("</b>");

            searchSourceBuilder.highlighter(highlightBuilder);
        }

        // 设置排序 格式：sort= 销量 saleCount_asc/desc || 热度分 hostScore_asc/desc || 价格 skuPrice_asc/desc
        String sort = searchParam.getSort();
        if(!StringUtils.isEmpty(sort)){
            // 分隔
            String[] s = sort.split("_");
            String attrName = s[0];
            String order = s[1];
            searchSourceBuilder.sort(attrName,SortOrder.fromString(order));
        }

        // 分页
        Integer pageNum = searchParam.getPageNum();
        if(pageNum != null){
            // 从哪开始：from：pageSize*(pageNum -1 )
            searchSourceBuilder.from((pageNum - 1) * EsConstant.PRODUCT_PAGE_SIZE);
            // 每页显示数
            searchSourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);
        }

        // 聚合
        // 商品聚合
        // 构建父聚合时必须将字段名另外设置（ES-BUG），否则字段名会被后面字段覆盖
//        TermsAggregationBuilder brandAggBuilder = AggregationBuilders.terms("brand-agg").field("brandId");
        TermsAggregationBuilder brandAggBuilder = AggregationBuilders.terms("brand-agg").field("brandId");
        // field字段不能将不能写错位置，如果写在了括号外面，代表的是父聚合的字段，大意了
        brandAggBuilder.subAggregation(AggregationBuilders.terms("brandImg-agg").field("brandImg"));
        brandAggBuilder.subAggregation(AggregationBuilders.terms("brandName-agg").field("brandName"));
        // 分类聚合
        TermsAggregationBuilder catalogAggBuilder = AggregationBuilders.terms("catlog-agg").field("catalogId");
        catalogAggBuilder.subAggregation(AggregationBuilders.terms("catlogName-agg").field("catalogName"));
        // 分类聚合
        NestedAggregationBuilder attrNestedAggBuilder = AggregationBuilders.nested("attr-agg", "attrs");

        TermsAggregationBuilder attrIdBuilder = AggregationBuilders
                // 聚合方式term聚合
                .terms("id-agg")
                // 聚合字段
                .field("attrs.attrId");

        attrNestedAggBuilder
                // 子聚合
                .subAggregation(attrIdBuilder);
        // 属性名应该为属性id聚合的子聚合
        attrIdBuilder
                // 子聚合
                .subAggregation(AggregationBuilders
                // 聚合方式term聚合
                .terms("name-agg")
                // 聚合字段
                .field("attrs.attrName"));
        // 属性值应该为属性id聚合的子聚合
        attrIdBuilder
                // 子聚合
                .subAggregation(AggregationBuilders
                // 聚合方式term聚合
                .terms("value-agg")
                // 聚合字段
                .field("attrs.attrValue"));

        // 将聚合条件添加到源中
        searchSourceBuilder.aggregation(brandAggBuilder);
        searchSourceBuilder.aggregation(catalogAggBuilder);
        searchSourceBuilder.aggregation(attrNestedAggBuilder);

        System.out.println(searchSourceBuilder);
        // 将源添加到请求中
        request.source(searchSourceBuilder);
        return request;
    }

    private SearchResult getResultBySearchRequest(SearchRequest searchRequest, HttpServletRequest request) throws IOException {

        System.out.println(searchRequest);
        // 获取请求中的参数
        String queryString = request.getQueryString();
        // 获取响应
        SearchResponse response = restHighLevelClient.search(searchRequest,RequestOptions.DEFAULT);
        // 分析响应结果，组装数据
        SearchResult result = new SearchResult();
        // 面包屑导航条
        List<SearchResult.NavVo> navVos = result.getNavVos();
        // 分页
        // 总记录数
        int total = (int) response.getHits().getTotalHits().value;
        // 总页数
        int totalPage =total % EsConstant.PRODUCT_PAGE_SIZE == 0 ? total / EsConstant.PRODUCT_PAGE_SIZE : total / EsConstant.PRODUCT_PAGE_SIZE + 1;
        // 从哪开始，默认第0个
        int from = searchRequest.source().from();
        // 当前页 ，如果from小于每页显示数，则当前页从0开始
        int current = 1;
        if(from > 0){
            // from 从0开始
            from = from + 1;
            current = from % EsConstant.PRODUCT_PAGE_SIZE == 0 ? from / EsConstant.PRODUCT_PAGE_SIZE : from / EsConstant.PRODUCT_PAGE_SIZE + 1;
        }
        result.setTotalPage(totalPage);
        result.setCurrent(current);
        result.setTotal(total);
        // 设置页码
        List<Integer> pageNums = new ArrayList<>();
        // 页码：每页显示5个页码
        //总页码 < 3 显示：1 [2] 3
        //5 > 总页码 > 3 显示： 2 3 【4】 5
        //最后5页 显示：n - 4   n - 3    n -2   [n - 1]    n
        //当前页码 ：[n]：
//        int totalPage = 0;
        if(totalPage < 5){
            // 当总页码小于5时，遍历到总页码
            for (int i = 1;i <= totalPage; i++){
                pageNums.add(i);
            }
        } else if(totalPage >= 5) {
            // 当总页码大于且不为最后5页(避免页码超出)时，设置5个页码
            if(totalPage - current >= 5){
                for (int i = 1;i <= 5; i++){
                    // 当当前页码大于3时，为：当前页码 - 2 + i
                    // 当当前页码小于3时，页码为0-5
                    if(current < 3){
                        pageNums.add(i);
                    } else {
                        pageNums.add(current - 2 + i);
                    }
                }
            } else {
                // 当为最后5页时，遍历最后5页
                for (int i = totalPage - 4;i <= totalPage ;i++ ){
                    pageNums.add(i);
                }
            }

        }
        result.setPageNums(pageNums);

        // 商品-product
        List<SkuESModel> products = new ArrayList<>();
        // 分析命中结果
        for (SearchHit hit : response.getHits().getHits()) {
            // 得到source
            String sourceAsString = hit.getSourceAsString();
            // 转换数据
            SkuESModel skuESModel = JSON.parseObject(sourceAsString, SkuESModel.class);
            // 设置skuTitle，带了keyword关键字才能设置——必须有高亮字段
            if(hit.getHighlightFields() != null && hit.getHighlightFields().size() > 0){
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                skuESModel.setSkuTitle(highlightFields.get("skuTitle").getFragments()[0].toString());
            }
            // 添加数据
            products.add(skuESModel);
        }
        result.setProducts(products);
        // 分析聚合元素
        // 聚合元素-属性
        ParsedNested attrAgg = response.getAggregations().get("attr-agg");
        // 获聚合元素
        Aggregations attrAggs = attrAgg.getAggregations();
        // 获取聚合元素中的buckets
//        ParsedStringTerms attrNameAgg = attrAggs.get("name-agg");
//        List<? extends Terms.Bucket> attrNameAggBuckets = attrNameAgg.getBuckets();

//        ParsedStringTerms attrValueAgg = attrAggs.get("value-agg");

        ParsedLongTerms attrIdAgg = attrAggs.get("id-agg");
        List<? extends Terms.Bucket> attrIdAggBuckets = attrIdAgg.getBuckets();
        // 创建attrs集合存储attr
        ArrayList<SearchResult.Attr> attrs = new ArrayList<>();
        for (int i = 0; i < attrIdAggBuckets.size(); i++) {
            Long attrId = attrIdAggBuckets.get(i).getKeyAsNumber().longValue();
            Aggregations aggregations = attrIdAggBuckets.get(i).getAggregations();
            // 获取id聚合的子聚合
            ParsedStringTerms attrNameAgg = aggregations.get("name-agg");
            // 获取id聚合的子聚合的属性名，一个id对应一个属性名
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            // 一个attrId对应的attrValue有多个
            ParsedStringTerms attrValAgg = aggregations.get("value-agg");
            List<String> attrVals = attrValAgg.getBuckets().stream()
                    // 获取每一个bucket中的值 Terms.Bucket::getKeyAsString
                    .map(bucket -> {
                        return bucket.getKeyAsString();
                    }).collect(Collectors.toList());

            // 设置面包屑 -- attr属性
            System.out.println(queryString);
            // 解析每个参数
            if(!StringUtils.isEmpty(queryString)){
                String[] split = queryString.split("&");
                if(split.length > 0){
                    // 遍历每个参数，判断是否以“attrs”开头 attrs=2_LIO-AN02
                    for (String s : split) {
                        if(s.startsWith("attrs")){
                            // 分隔属性
                            String[] attrValues = s.split("=");
                            String attr = attrValues[1];
                            // 分隔attr 2_LIO-AN02
                            String[] s1 = attr.split("_");
                            String attrVal = s1[1];
                            // 转码
                            attrVal = URLEncoder.encode(attrVal, "UTF-8");
                            // 由于空格在java中会转义为 + 而浏览器将其空格转义为 20% 故必须将其替换为20%
                            attrVal.replace("+", "20%");
                            // 判断集合中是否有其存在
                            if(attrVals.contains(attrVal)){
                                // 设置面包屑值
                                SearchResult.NavVo navVo = new SearchResult.NavVo();
                                navVo.setNavName(attrName);
                                navVo.setNavValue(attrVal); // 前台需要完整数据attr
//                                navVo.setNavValue(attr);
                                // 设置连接地址 将 &attrs=2_LIO-AN02
                                String link = null;
                                // 设置连接地址 可能brandId前面没有&
                                if(s.contains("&")){
                                    link = queryString.replace("&" + s,"");
                                } else {
                                    link = queryString.replace(s,"");
                                }
                                navVo.setLink(link);
                                navVos.add(navVo);
                            }
                        }
                    }
                }
            }

            // 设置attr属性
            SearchResult.Attr attr = new SearchResult.Attr();
            attr.setAttrId(attrId);
            attr.setAttrName(attrName);
            attr.setAttrValue(attrVals);

            attrs.add(attr);

        }
        result.setAttrs(attrs);

        // 聚合元素-品牌
        List<SearchResult.Brand> brands = new ArrayList<SearchResult.Brand>();
        ParsedLongTerms brandAgg = response.getAggregations().get("brand-agg");
        // 获取key对应的id
        List<? extends Terms.Bucket> buckets = brandAgg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            SearchResult.Brand brand = new SearchResult.Brand();
            // id
            Long brandId = (Long) bucket.getKeyAsNumber();
            brand.setBrandId(brandId);
            // 获取聚合元素
            Aggregations aggregations = bucket.getAggregations();
            ParsedStringTerms brandImgAgg = aggregations.get("brandImg-agg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
            // 设置图片
            brand.setBrandImg(brandImg);
            ParsedStringTerms brandNameAgg = aggregations.get("brandName-agg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            // 设置图片
            brand.setBrandName(brandName);

            brands.add(brand);

        }
        result.setBrands(brands);

        // 聚合元素-三级分类
        List<SearchResult.Catelog> categorys = new ArrayList<SearchResult.Catelog>();
        ParsedLongTerms catlogAgg = response.getAggregations().get("catlog-agg");
        // 获取key对应的id
        List<? extends Terms.Bucket> catlogAggBuckets = catlogAgg.getBuckets();
        for (Terms.Bucket bucket : catlogAggBuckets) {
            SearchResult.Catelog catelog = new SearchResult.Catelog();
            // id
            Long catelogId = (Long) bucket.getKeyAsNumber();
            catelog.setCatalogId(catelogId);
            // 获取聚合元素
            Aggregations aggregations = bucket.getAggregations();
            ParsedStringTerms brandImgAgg = aggregations.get("catlogName-agg");
            String catlogName = brandImgAgg.getBuckets().get(0).getKeyAsString();
            // 设置图片
            catelog.setCatalogName(catlogName);
            categorys.add(catelog);
        }
        result.setCategorys(categorys);

        // 解析每个参数
        if(!StringUtils.isEmpty(queryString)){
            String[] split = queryString.split("&");
            if(split.length > 0){
                // 遍历每个参数，判断是否以“attrs”开头 attrs=2_LIO-AN02
                for (String s : split) {
                    if(s.startsWith("brandId")){
                        // 分隔属性
                        String[] brand = s.split("=");
                        String brandId = brand[1];
                        // 判断集合中是否有其存在
                        for (SearchResult.Brand brand1 : brands) {
                            if(brandId.equals(brand1.getBrandId().toString())){
                                // 设置面包屑值
                                SearchResult.NavVo navVo = new SearchResult.NavVo();
                                // 硬性编码为品牌，不需要另外设置
                                navVo.setNavName("品牌");
                                navVo.setNavValue(brand1.getBrandName());
                                String link = null;
                                // 设置连接地址 可能brandId前面没有&
                                if(s.contains("&")){
                                    link = queryString.replace("&" + s,"");
                                } else {
                                    link = queryString.replace(s,"");
                                }
                                navVo.setLink(link);
                                // 将其添加到所有导航条中
                                navVos.add(navVo);
                            }
                        }
                    } else if (s.startsWith("catlogId")){
                        // 分隔属性
                        String[] catelogs = s.split("=");
                        String catlogId = catelogs[1];
                        // 判断集合中是否有其存在
                        for (SearchResult.Catelog catelog : categorys) {
                            // 进行equals比较时，必须先进行比较是否是同类型的
                            // catelog.getCatalogId()为Long类型，进行比较时，会判断其是否属于Long类型 obj instanceof Long
                            // 而catlogId为String类型，也会进行obj instanceof Long比较 必须转为String类型
                            if(catlogId.equals(catelog.getCatalogId().toString())){
                                // 设置面包屑值
                                SearchResult.NavVo navVo = new SearchResult.NavVo();
                                navVo.setNavName("分类");
                                navVo.setNavValue(catelog.getCatalogName());
                                String link = null;
                                // 设置连接地址 可能brandId前面没有&
                                if(s.contains("&")){
                                    link = queryString.replace("&" + s,"");
                                } else {
                                    link = queryString.replace(s,"");
                                }
                                navVo.setLink(link);
                                navVos.add(navVo);
                            }
                        }
                    }
                }
            }
        }

        // 组装集合数据
        return result;
    }

    private SearchRequest createSearchRequestByParam1(SearchParam searchParam) {
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
        if(!StringUtils.isEmpty(searchParam.getHasStock())){
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

        // 排序
        if (!StringUtils.isEmpty(searchParam.getSort())) {

            String[] split = searchParam.getSort().split("_");
            String field = split[0];
            String order = split[1];
            if(order.equalsIgnoreCase("asc")){
                sourceBuilder.sort(field, SortOrder.ASC);
            } else {
                sourceBuilder.sort(field, SortOrder.DESC);
            }
        }
        // 分页
        Integer pageNum = searchParam.getPageNum();
        if(pageNum != null){
            // 第几页：pageNum  从哪开始：(pageNum - 1)*pageSize
            sourceBuilder.from((pageNum - 1)*EsConstant.PRODUCT_PAGE_SIZE);
            sourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);
        }

        // 高亮显示
       /* HighlightBuilder skuTitle = sourceBuilder.highlighter().field("skuTitle");
        skuTitle.preTags("<b style='color=red'>").postTags("</b>");*/

        // 聚合

        // 品牌聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand-agg");
        brandAgg.field("brandId");
        brandAgg.subAggregation(AggregationBuilders.terms("brandImg-agg").field("brandImg").size(10));
        brandAgg.subAggregation(AggregationBuilders.terms("brandName-agg").field("brandName").size(10));
        // 添加聚合条件
        sourceBuilder.aggregation(brandAgg);

        // 分类聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catlog-agg");
        catalogAgg.field("catalogId");
        catalogAgg.subAggregation(AggregationBuilders.terms("catlogName-agg").field("catalogName"));
        sourceBuilder.aggregation(catalogAgg);

        // 属性聚合

        TermsAggregationBuilder idAgg = AggregationBuilders.terms("id-agg");
        idAgg.field("attrs.attrId");
        idAgg.subAggregation(AggregationBuilders.terms("name-agg").field("attrs.attrName").size(10));
        idAgg.subAggregation(AggregationBuilders.terms("value-agg").field("attrs.attrValue").size(10));


        sourceBuilder.aggregation(AggregationBuilders
                .nested("attr-agg", "attrs")
                .subAggregation(idAgg));

        sourceBuilder.query(queryBuilder);
        System.out.println(sourceBuilder);

        request.source(sourceBuilder);

        return request;
    }

}
