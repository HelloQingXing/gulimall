package com.qx.gulimall.coupon.controller;

import java.util.Arrays;
import java.util.Map;

import com.qx.common.dto.SpuBoundDTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.qx.gulimall.coupon.entity.SpuBoundsEntity;
import com.qx.gulimall.coupon.service.SpuBoundsService;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.R;



/**
 * 商品spu积分设置
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 19:54:15
 */
@RestController
@RequestMapping("coupon/spubounds")
public class SpuBoundsController {
    @Autowired
    private SpuBoundsService spuBoundsService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("coupon:spubounds:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuBoundsService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("coupon:spubounds:info")
    public R info(@PathVariable("id") Long id){
		SpuBoundsEntity spuBounds = spuBoundsService.getById(id);

        return R.ok().put("spuBounds", spuBounds);
    }


    /**
     * 保存boundDTo
     * @param boundDTo
     * @return
     */
    @PostMapping("/save/spuBoundDTo")
    R saveSpuBoundDTo(@RequestBody SpuBoundDTo boundDTo){

        spuBoundsService.saveSpuBoundDTo(boundDTo);

        return R.ok();
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("coupon:spubounds:save")
    public R save(@RequestBody SpuBoundsEntity spuBounds){
		spuBoundsService.save(spuBounds);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("coupon:spubounds:update")
    public R update(@RequestBody SpuBoundsEntity spuBounds){
		spuBoundsService.updateById(spuBounds);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("coupon:spubounds:delete")
    public R delete(@RequestBody Long[] ids){
		spuBoundsService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
