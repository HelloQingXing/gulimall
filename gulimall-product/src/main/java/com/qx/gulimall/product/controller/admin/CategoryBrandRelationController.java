package com.qx.gulimall.product.controller.admin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.qx.gulimall.product.entity.CategoryBrandRelationEntity;
import com.qx.gulimall.product.service.CategoryBrandRelationService;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @PostMapping("save")
    public R saveDetail(@RequestBody CategoryBrandRelationEntity categoryBrandRelationEntity){

        categoryBrandRelationService.saveDetail(categoryBrandRelationEntity);

        return R.ok();

    }

    /**
     * 通过当前商品ID获取当前关联信息
     * @RequestParam("brandId") Long brandId 如果有一个uri连接完全相同，而requestparam参数不同也不能使用
     */
/*    @GetMapping("/brands/list")
    public R brandsList(@RequestParam("brandId") Long brandId){

        List<CategoryBrandRelationEntity> relationEntityList = categoryBrandRelationService.list(new QueryWrapper<CategoryBrandRelationEntity>().eq("brand_id", brandId));

        return R.ok().put("data", relationEntityList);
    }*/

    /**
     * 通过当前ID获取当前关联信息
     */
    @GetMapping("/brands/list")
    public R brandsListByCatId(@RequestParam("catId") Long catId){

        List<CategoryBrandRelationEntity> relationEntityList = categoryBrandRelationService.listByCatId(catId);

        return R.ok().put("data", relationEntityList);
    }

    /**
     * 列表
     */
    @RequestMapping("/catelog/list")
    public R catelogList(@RequestParam("brandId") Long brandId){

        List<CategoryBrandRelationEntity> categoryBrandRelationEntityList = categoryBrandRelationService.listByBrandId(brandId);

        return R.ok().put("data", categoryBrandRelationEntityList);
    }

    /**
     * 列表
     */
    @RequestMapping("/brands/list")
    public R list(@RequestParam Map<String, Object> params){

        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.save(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
