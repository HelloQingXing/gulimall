package com.qx.gulimall.product.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.product.dao.ProductAttrValueDao;
import com.qx.gulimall.product.entity.ProductAttrValueEntity;
import com.qx.gulimall.product.service.ProductAttrValueService;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void updateBatchBySpuId(Long spuId, List<ProductAttrValueEntity> productAttrValueEntityList) {
        // 封装条件
        QueryWrapper<ProductAttrValueEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);


        productAttrValueEntityList.forEach(entity -> {
            // 为每一个ProductAttrValueEntity设置属性
            ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
            BeanUtils.copyProperties(entity, productAttrValueEntity);
            productAttrValueEntity.setSpuId(spuId);

            this.baseMapper.updateBySpuId(productAttrValueEntity);
        });

//        this.baseMapper.updateBatchBySpuId(attrValueEntityList); //  mybatis不能在一个sql语句中执行多个更新操作

    }

}