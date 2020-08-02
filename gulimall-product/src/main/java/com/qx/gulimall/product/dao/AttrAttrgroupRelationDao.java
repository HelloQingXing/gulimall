package com.qx.gulimall.product.dao;

import com.qx.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {

    void deleteByList(List<AttrAttrgroupRelationEntity> relationList);
}
