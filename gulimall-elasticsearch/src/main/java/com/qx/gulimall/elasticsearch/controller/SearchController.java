package com.qx.gulimall.elasticsearch.controller;

import com.qx.common.utils.R;
import com.qx.gulimall.elasticsearch.service.MallSearchService;
import com.qx.gulimall.elasticsearch.service.impl.MallSearchServiceImpl;
import com.qx.gulimall.elasticsearch.vo.SearchParam;
import com.qx.gulimall.elasticsearch.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Classname SearchController
 * @Description 商品检索
 * @Date 2020/7/31 23:25
 * @Created by 卿星
 */
@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    @GetMapping(value = {"/","list.html"})
    public String search(SearchParam searchParam, Model model, HttpServletRequest request){

        SearchResult result = null;
            result = mallSearchService.searchByParam(searchParam , request);
        model.addAttribute("data",result);

        return "list";
    }

}
