package com.qx.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qx.common.dto.SpuBoundDTo;
import com.qx.common.utils.PageUtils;
import com.qx.gulimall.coupon.entity.SpuBoundsEntity;

import java.util.Map;

/**
 * 商品spu积分设置
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 19:54:15
 */
public interface SpuBoundsService extends IService<SpuBoundsEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuBoundDTo(SpuBoundDTo boundDTo);
}

