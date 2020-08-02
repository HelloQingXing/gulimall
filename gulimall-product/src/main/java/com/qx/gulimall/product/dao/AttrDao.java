package com.qx.gulimall.product.dao;

import com.qx.gulimall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品属性
 * 
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
@Mapper
public interface AttrDao extends BaseMapper<AttrEntity> {

    List<Long> selectAttrIdsIsSearch(List<Long> attrIds);
}
