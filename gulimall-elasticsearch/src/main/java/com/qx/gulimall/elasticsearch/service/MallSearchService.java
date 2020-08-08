package com.qx.gulimall.elasticsearch.service;

import com.qx.gulimall.elasticsearch.vo.SearchParam;
import com.qx.gulimall.elasticsearch.vo.SearchResult;

import javax.servlet.http.HttpServletRequest;

/**
 * @Classname MallSearchService
 * @Description 商城检索
 * @Date 2020/8/1 10:38
 * @Created by 卿星
 */
public interface MallSearchService {
    SearchResult searchByParam(SearchParam searchParam, HttpServletRequest request);
}
