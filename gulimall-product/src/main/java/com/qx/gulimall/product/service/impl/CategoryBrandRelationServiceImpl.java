package com.qx.gulimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.qx.gulimall.product.entity.BrandEntity;
import com.qx.gulimall.product.entity.CategoryEntity;
import com.qx.gulimall.product.service.BrandService;
import com.qx.gulimall.product.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.product.dao.CategoryBrandRelationDao;
import com.qx.gulimall.product.entity.CategoryBrandRelationEntity;
import com.qx.gulimall.product.service.CategoryBrandRelationService;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }


    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelationEntity) {
        // 获取品牌
        BrandEntity brandEntity = brandService.getById(categoryBrandRelationEntity.getBrandId());
        // 获取分类
        CategoryEntity categoryEntity = categoryService.getById(categoryBrandRelationEntity.getCatelogId());
        // 设置属性
        categoryBrandRelationEntity.setBrandName(brandEntity.getName());
        categoryBrandRelationEntity.setCatelogName(categoryEntity.getName());
        this.save(categoryBrandRelationEntity);
    }

    @Override
    public List<CategoryBrandRelationEntity> listByBrandId(Long brandId) {

        // 组装条件
        QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<>();
        // 是否需要查询指定id的表，根据判断前端传来的brandId是否需要加上
        if(brandId != 0){
            wrapper.eq("brand_id", brandId);
        }

        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<CategoryBrandRelationEntity> listByCatId(Long catId) {

        QueryWrapper<CategoryBrandRelationEntity> wrapper = new QueryWrapper<CategoryBrandRelationEntity>();
        // 是否需要组装id
        if(catId != 0){
            wrapper.eq("catelog_id", catId);
        }


        List<CategoryBrandRelationEntity> brandRelationList = baseMapper.selectList(wrapper);
        // 去重
        // 设置存储brandId的集合
//        List<Long> branIdList = new ArrayList<>();
        HashSet<Long> branIdList = new HashSet<>();
        List<CategoryBrandRelationEntity> collect = brandRelationList.stream().filter(brandRelation -> {
            // 查看branIdList是否有当前brandId
            Long brandId = brandRelation.getBrandId();
            boolean b = branIdList.contains(brandId);
            if(!b){
                // 将当前id添加到集合中
                branIdList.add(brandId);
                return true;
            }else {
                return false;
            }
        })/*.map(brandRelation -> {

            // 将除了brandId和brandname外其他字段去重
            brandRelation.setId(null);
            brandRelation.setCatelogId(null);
            brandRelation.setCatelogName(null);
            return brandRelation;
        })*/.collect(Collectors.toList());

        return collect;
    }

}