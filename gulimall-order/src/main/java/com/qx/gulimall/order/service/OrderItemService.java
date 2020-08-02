package com.qx.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qx.common.utils.PageUtils;
import com.qx.gulimall.order.entity.OrderItemEntity;

import java.util.Map;

/**
 * 订单项信息
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 19:26:09
 */
public interface OrderItemService extends IService<OrderItemEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

