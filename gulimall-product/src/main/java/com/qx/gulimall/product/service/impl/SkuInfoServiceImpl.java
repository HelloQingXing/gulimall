package com.qx.gulimall.product.service.impl;

import com.qx.common.constant.ProductConstant;
import com.qx.common.dto.StockVo;
import com.qx.common.dto.es.SkuESModel;
import com.qx.common.utils.R;
import com.qx.gulimall.product.entity.*;
import com.qx.gulimall.product.feign.ESFeignService;
import com.qx.gulimall.product.feign.WareFeignService;
import com.qx.gulimall.product.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.product.dao.SkuInfoDao;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private ProductAttrValueService attrValueService;

    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private ESFeignService esFeignService;

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        // page=1&limit=10&key=&catelogId=0&brandId=0&min=0&max=0
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        // 组装条件
        Object key = params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w -> {
                w.like("sku_name",key);
            });
        }
        Object catelogId = params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)){
            // id为0时，不设条件
            if(Integer.parseInt(catelogId.toString()) != 0){
                wrapper.eq("catalog_id",catelogId);
            }
        }
        Object brandId = params.get("brandId");
        if(!StringUtils.isEmpty(brandId)){
            // id为0时，不设条件
            if(Integer.parseInt(brandId.toString()) != 0){
                wrapper.eq("brand_id",brandId);
            }
        }
        Object min = params.get("min");
        if(!StringUtils.isEmpty(min)){
            wrapper.ge("price",min);
        }
        Object max = params.get("max");
        if(!StringUtils.isEmpty(max) && max.equals(0)){
            wrapper.le("price",max);
        }

        IPage<SkuInfoEntity> page =
                this.page(new Query<SkuInfoEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }


    @Override
    public List<SkuInfoEntity> getSkuInfoBySpuId(Long spuId) {

        // 设置条件
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);

        return this.list(wrapper);
    }

    @Override
    public Boolean changeProductStatusBySpuId(Long spuId,Integer status) {

        Integer count = baseMapper.updateProductStatusBySpuId(spuId,status);
        return count > 0;
    }
}