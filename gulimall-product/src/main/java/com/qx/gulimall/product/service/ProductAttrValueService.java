package com.qx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qx.common.utils.PageUtils;
import com.qx.gulimall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateBatchBySpuId(Long spuId, List<ProductAttrValueEntity> productAttrValueEntityList);

    List<ProductAttrValueEntity> listByAttrIds(List<Long> attrIds);

    List<ProductAttrValueEntity> listByAttrIdsAndSpuId(List<Long> attrIds, Long spuId);
}

