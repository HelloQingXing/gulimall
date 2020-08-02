package com.qx.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qx.common.utils.PageUtils;
import com.qx.gulimall.coupon.entity.SeckillPromotionEntity;

import java.util.Map;

/**
 * 秒杀活动
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 19:54:16
 */
public interface SeckillPromotionService extends IService<SeckillPromotionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

