package com.qx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qx.gulimall.product.entity.CategoryBrandRelationEntity;
import com.qx.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.product.dao.BrandDao;
import com.qx.gulimall.product.entity.BrandEntity;
import com.qx.gulimall.product.service.BrandService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {

        QueryWrapper<BrandEntity> wrapper = new QueryWrapper<>();
        if(params != null && params.size() > 0){
            // 获取请求参数并封装
            Object key = params.get("key");
            if(!StringUtils.isEmpty(key)){
                wrapper.eq("brand_id",key).or().like("name",key);
            }

        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateDetail(BrandEntity brand) {

        if(!StringUtils.isEmpty(brand.getName())){
            // 更改冗余存储的信息,保证冗余字段一致
            brandService.updateBrand(brand.getBrandId(),brand.getName());
        }


        // 更改品牌信息
        this.save(brand);
    }

    @Override
    public void updateBrand(Long brandId, String name) {
        UpdateWrapper<BrandEntity> wrapper = new UpdateWrapper<BrandEntity>().eq("brand_id", brandId);

        this.update(wrapper);
    }

    @Override
    public void removeBrand(Long... brandIds) {

        // 将其转化为集合
        List<Long> brandIdList = Arrays.asList(brandIds);
        // 删除品牌
        baseMapper.deleteBatchIds(brandIdList);
        // 创建包装类
        QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<>();
        // 删除关联
        brandIdList.stream().map(brandId -> {
            wrapper.eq("brand_id", brandId);
            return brandId;
        }).collect(Collectors.toList());
        // 删除关联
        categoryBrandRelationService.remove(wrapper);

    }


}