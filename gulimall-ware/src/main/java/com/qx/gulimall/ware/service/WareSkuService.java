package com.qx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qx.common.dto.StockVo;
import com.qx.common.utils.PageUtils;
import com.qx.gulimall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 20:10:59
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(WareSkuEntity wareSkuEntity);

    boolean hasStock(Long skuId);

    List<StockVo> hasStockBySkuIds(List<Long> skuIdList);
}

