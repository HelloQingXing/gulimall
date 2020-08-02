package com.qx.gulimall.product.controller.admin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import com.qx.gulimall.product.entity.CategoryEntity;
import com.qx.gulimall.product.service.CategoryService;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.R;



/**
 * 商品三级分类
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
@RestController
@RequestMapping("/product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 查出所有分类以及子分类，以树形结构组装起来
     */
    @RequestMapping("/list/tree")
    public R list(){
        List<CategoryEntity> categoryList = categoryService.listTree();

        return R.ok().put("data", categoryList);
    }

    @PostMapping("assign/order")
    public R assignOrder(@RequestBody CategoryEntity categoryEntity){


        return R.ok();
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 批量修改层级
     * @param categoryEntities
     * @return
     */
    @PostMapping("/update/batch")
    public R updateBatch(@RequestBody CategoryEntity[] categoryEntities){

        categoryService.updateBatch(categoryEntities);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateCascade(category);

        return R.ok();
    }

    /**
     * 删除
     * 1)、带有@RequestBody的，不能使用GetMapping
     * 2）、SringMVC会自动将请求体的JSON数据转为对应的对象
     *
     * 逻辑删除
     *  1）、配置全局逻辑删除配置
     *  2）、配置具体字段CategoryEntity -> showStatus逻辑删除@TableLogic
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] catIds){

        // 1、检查删除的菜单，是否被其他地方引用

		categoryService.removeMenuByIdList(catIds);

        return R.ok();
    }

    @DeleteMapping("remove/batch")
    public R batchRemove(@RequestBody List<Integer> idList){

        categoryService.removeByIds(idList);

        return R.ok();
    }

}
