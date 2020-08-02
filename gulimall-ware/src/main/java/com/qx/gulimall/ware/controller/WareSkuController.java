package com.qx.gulimall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.qx.common.dto.StockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.qx.gulimall.ware.entity.WareSkuEntity;
import com.qx.gulimall.ware.service.WareSkuService;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.R;



/**
 * 商品库存
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 20:10:59
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 是否还有库存
     */
    @GetMapping("has-stoke/{skuId}")
    public R hasStock(@PathVariable Long skuId){
        boolean flag = wareSkuService.hasStock(skuId);

        return R.ok().put("data",flag);
    }


    /**
     * 是否还有库存
     */
    @PostMapping("/has-stoke")
    public R hasStockBySkuIds(@RequestBody List<Long> skuIdList){
        List<StockVo> stockVoList = wareSkuService.hasStockBySkuIds(skuIdList);

        R ok = R.ok();
        R r = ok.setData(stockVoList);
        return r;
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
