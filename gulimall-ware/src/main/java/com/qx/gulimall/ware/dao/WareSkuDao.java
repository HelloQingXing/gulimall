package com.qx.gulimall.ware.dao;

import com.qx.common.dto.StockVo;
import com.qx.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存
 * 
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 20:10:59
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    void updateStock(@Param("skuId") Long skuId,@Param("wareId")  Long wareId,@Param("stock")  Integer stock);

    StockVo selectStockBySkuId(Long skuId);
}
