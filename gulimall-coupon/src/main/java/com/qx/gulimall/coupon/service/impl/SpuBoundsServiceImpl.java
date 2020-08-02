package com.qx.gulimall.coupon.service.impl;

import com.qx.common.dto.SpuBoundDTo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.coupon.dao.SpuBoundsDao;
import com.qx.gulimall.coupon.entity.SpuBoundsEntity;
import com.qx.gulimall.coupon.service.SpuBoundsService;


@Service("spuBoundsService")
public class SpuBoundsServiceImpl extends ServiceImpl<SpuBoundsDao, SpuBoundsEntity> implements SpuBoundsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuBoundsEntity> page = this.page(
                new Query<SpuBoundsEntity>().getPage(params),
                new QueryWrapper<SpuBoundsEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSpuBoundDTo(SpuBoundDTo boundDTo) {

        // 复制属性
        SpuBoundsEntity spuBoundsEntity = new SpuBoundsEntity();
        BeanUtils.copyProperties(boundDTo,spuBoundsEntity);

        // 执行保存
        this.save(spuBoundsEntity);
    }

}