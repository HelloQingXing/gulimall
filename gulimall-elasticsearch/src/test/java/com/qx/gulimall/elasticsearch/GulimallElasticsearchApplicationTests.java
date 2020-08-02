package com.qx.gulimall.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.google.errorprone.annotations.Var;
import com.qx.gulimall.elasticsearch.config.ElasticSearchConfig;
import lombok.Data;
import org.apache.http.HttpRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.mapper.MapperService;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.support.ValueType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class GulimallElasticsearchApplicationTests {

  @Autowired
  RestHighLevelClient restHighLevelClient;

  @Test
  void contextLoads() {

    System.out.println(restHighLevelClient);
  }

  @Test
  void indexPost() throws IOException {

    @Data
    class User{
      String name;
      Integer age;
      String sex;
      String message;
    }

    /*HashMap<String, Object> map = new HashMap<>(10);

    map.put("username","qqx");
    map.put("age",22);
    map.put("sex","男");
    map.put("hobby","play");*/

    IndexRequest indexRequest = new IndexRequest("user");
    indexRequest.id("1");

//    IndexRequest source = indexRequest.source("username","qqx","age",22,"sex","男");
//    System.out.println(source);
//    System.out.println(indexRequest == source);

    User user = new User();
    user.setName("qqxx");
    user.setAge(20);
    user.setSex("男");
    user.setMessage("qqx is very cool !");

    String parse = JSON.toJSONString(user);


    indexRequest.source(parse,XContentType.JSON);


    IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

    System.out.println(index);

  }

  @Test
  void searchRequest() throws IOException {
    // 创建搜索请求对象
    SearchRequest searchRequest = new SearchRequest();
    // 创建构造器
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    // 构建查询条件
    searchSourceBuilder.query(QueryBuilders.matchAllQuery());
    // 添加条件到请求体中
    SearchRequest source = searchRequest.source(searchSourceBuilder);

    // 执行
    SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

    System.out.println(search);
  }

  @Data
  public static class _source {

    private int account_number;
    private int balance;
    private String firstname;
    private String lastname;
    private int age;
    private String gender;
    private String address;
    private String employer;
    private String email;
    private String city;
    private String state;
  }

  // ## 按照年龄聚合，并求这些年龄段的人的平均薪资
  @Test
  void searchRequestByCondition() throws IOException {
    // 创建搜索请求对象,指定索引
    SearchRequest searchRequest = new SearchRequest("bank");
    // 创建构造器
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    // 构建查询条件
//    searchSourceBuilder.query(QueryBuilders.matchAllQuery());
    // 指定聚合方式，按年龄聚合，类型为Long
    TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms("agg_age").field("age");
    aggregationBuilder.subAggregation(AggregationBuilders.avg("average_blance").field("blance"));
    SearchSourceBuilder aggregation = searchSourceBuilder.aggregation(aggregationBuilder);

    // 添加条件到请求体中
    SearchRequest source = searchRequest.source(searchSourceBuilder);

    // 执行
    SearchResponse search = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
    // 获取所有匹配成功的数据
    SearchHits hits = search.getHits();
    SearchHit[] hits1 = hits.getHits();
    // 遍历，取出数据
    for (SearchHit documentFields : hits1) {
      // 将数据转为json字符串
      String sourceAsString = documentFields.getSourceAsString();
      // 将字符串转为javabean对象
      _source source1 = JSON.parseObject(sourceAsString, _source.class);
      System.out.println("结果集对象："+source1);
    }
    // 取出聚合结果
    Aggregations aggregations = search.getAggregations();
    List<Aggregation> aggregations1 = aggregations.asList();
    for (Aggregation aggregation1 : aggregations1) {
      String type = aggregation1.getType();
      String name = aggregation1.getName();
      Map<String, Object> metaData = aggregation1.getMetaData();
      System.out.println("聚合结果：type:" + type);
      System.out.println("聚合结果：name:" + name);
      System.out.println("聚合结果：metaData:" + metaData);
    }

  }

  @Test
  void test(){
    ArrayList<Integer> list = new ArrayList<>();
    for (int i = 0; i < 20; i++) {
      list.add(i);
    }
    List<Integer> collect = list.stream().map(item -> {
      if (item % 2 == 0) {
        System.out.println("当前遍历数：" + item);
        return item;
      } else {
        System.out.println("不能被2整除的数：" + item);
        return null;
      }
    }).collect(Collectors.toList());
    System.out.println("集合：" + collect);
    System.out.println("大小：" + collect.size());
    // 集合：[0, null, 2, null, 4, null, 6, null, 8, null, 10, null, 12, null, 14, null, 16, null, 18, null]
    // 大小：20
    // 由此可知：当返回为null时，仍然会放入集合中，集合大小也不会减少
  }

  @Test
  void test2(){
    char c1 = 128;
    char c2 = 128;
    System.out.println(c1 == c2);
  }


  @Test
  void test3(){
    String s1 = "hello";
    String s2 = "hello";
    System.out.println("s1 == ? s2" + s1 == s2);

    Character c1[] = {'h','e','l','l','o'};
    System.out.println("s1 == ? c1" + s1.equals(c1));

    System.out.println(1/2);
  }

  abstract class A{
//    private String a;
    public String geta(String b) { return  b;};
  }

  class B{
    void test(){
//      private String a = "";
//      System.out.println(a.length());
    }
  }
}
