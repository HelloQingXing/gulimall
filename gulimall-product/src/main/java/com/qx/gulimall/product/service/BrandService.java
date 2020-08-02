package com.qx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qx.common.utils.PageUtils;
import com.qx.gulimall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateDetail(BrandEntity brand);

    void updateBrand(Long brandId, String name);

    void removeBrand(Long... brandIds);
}

