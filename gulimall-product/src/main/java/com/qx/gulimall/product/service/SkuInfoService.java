package com.qx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qx.common.utils.PageUtils;
import com.qx.gulimall.product.entity.SkuInfoEntity;

import java.util.List;
import java.util.Map;

/**
 * sku信息
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
public interface SkuInfoService extends IService<SkuInfoEntity> {

    PageUtils queryPageByCondition(Map<String, Object> params);

    List<SkuInfoEntity> getSkuInfoBySpuId(Long spuId);

    Boolean changeProductStatusBySpuId(Long spuId,Integer status);
}

