package com.qx.gulimall.order.dao;

import com.qx.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 19:26:09
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
