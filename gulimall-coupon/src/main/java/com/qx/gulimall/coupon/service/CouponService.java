package com.qx.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qx.common.utils.PageUtils;
import com.qx.gulimall.coupon.entity.CouponEntity;

import java.util.Map;

/**
 * 优惠券信息
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 19:54:16
 */
public interface CouponService extends IService<CouponEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

