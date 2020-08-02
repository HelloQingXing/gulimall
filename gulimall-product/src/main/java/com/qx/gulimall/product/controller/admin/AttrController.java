package com.qx.gulimall.product.controller.admin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.qx.gulimall.product.entity.ProductAttrValueEntity;
import com.qx.gulimall.product.entity.vo.AttrResVo;
import com.qx.gulimall.product.entity.vo.AttrVo;
import com.qx.gulimall.product.service.AttrGroupService;
import com.qx.gulimall.product.service.CategoryService;
import com.qx.gulimall.product.service.ProductAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.qx.gulimall.product.entity.AttrEntity;
import com.qx.gulimall.product.service.AttrService;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.R;

/**
 * 商品属性
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private AttrGroupService attrGroupService;

    /**
     * 获取规格参数属性/分类销售属性
     * @param params 分页参数
     * @param catelogId 分类id
     * @param type  参数类型
     * @return
     */
    @GetMapping("/{type}/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable Long catelogId,
                  @PathVariable String type){
        PageUtils page = attrService.queryTypePage(params,catelogId,type);

        return R.ok().put("page", page);
    }

    /**
     * 获取spu规格
     * /product/attr/base/listforspu/{spuId}
     * @return
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R list(@PathVariable Long spuId){
        List<ProductAttrValueEntity> productAttrValueEntityList =  attrService.listforspuBySpuId(spuId);

        return R.ok().put("data",productAttrValueEntityList);
    }

    /**
     * 修改商品规格
     * /product/attr/update/{spuId}
     * @return
     */
    @RequestMapping("/update/{spuId}")
    public R updateBySpuId(@PathVariable Long spuId,
                           @RequestBody List<ProductAttrValueEntity> productAttrValueEntityList){

        productAttrValueService.updateBatchBySpuId(spuId,productAttrValueEntityList);

        return R.ok();
    }


    /**
     * 获取分类销售属性
     */
    /*@GetMapping("/sale/list/{catelogId}")
    public R saveList(@RequestParam Map<String, Object> params,
                  @PathVariable Long catelogId){
        PageUtils page = attrService.querySalePage(params,catelogId);

        return R.ok().put("page", page);
    }*/


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
        AttrResVo attr = attrService.getInfoById(attrId);

        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attr){
		attrService.saveAttrVo(attr);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrEntity attr){
//		attrService.updateAttr(attrResVo);
        attrService.updateById(attr);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));

        return R.ok();
    }

}
