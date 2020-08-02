package com.qx.gulimall.product.controller.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.qx.common.valid.AddGroup;
import com.qx.common.valid.UpdateGroup;
import com.qx.common.valid.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qx.gulimall.product.entity.BrandEntity;
import com.qx.gulimall.product.service.BrandService;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.R;

import javax.validation.Valid;


/**
 * 品牌
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
@RestController
@RequestMapping("product/brand")

@Validated
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 保存
     * @Valid：如果想使用效验规则，则必须添加该注解
     * 如果字段使用Groups指定了，那么必须使用@Validated注解，并且指定group接口类
     */
    @RequestMapping("/save")
    public R save( @RequestBody /*@Valid*/ @Validated({AddGroup.class}) BrandEntity brand/*,BindingResult bindingResult*/){
        // 使用全局异常
        /*System.out.println(bindingResult);
        // 判断结果是否有错
        if(bindingResult.hasErrors()){
            Map<String, String> resultMap = new HashMap<>();
            // 遍历错误并封装到Map中
            bindingResult.getFieldErrors().forEach((item) -> {
                String message = item.getDefaultMessage();
                String field = item.getField();
                resultMap.put(field,message);
            });
            return R.error(400,"提交数据不合法！").put("data",resultMap);
        } else {*/
            brandService.save(brand);
            return R.ok();
//        }
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@Validated(value = UpdateGroup.class) @RequestBody BrandEntity brand){
		brandService.updateDetail(brand);

        return R.ok();
    }

    /**
     * 修改状态
     */
    @RequestMapping("/update/status")
    public R updateStatus(@Validated(value = UpdateStatusGroup.class) @RequestBody BrandEntity brand){
		brandService.updateById(brand);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeBrand(brandIds);

        return R.ok();
    }

}
