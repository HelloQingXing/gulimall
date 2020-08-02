package com.qx.gulimall.product.dao;

import com.qx.gulimall.product.entity.SkuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * sku信息
 * 
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
@Mapper
public interface SkuInfoDao extends BaseMapper<SkuInfoEntity> {

    Integer updateProductStatusBySpuId(@Param("spuId") Long spuId, @Param("status") Integer status);
}
