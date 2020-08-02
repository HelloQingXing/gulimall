package com.qx.gulimall.product.dao;

import com.qx.gulimall.product.entity.ProductAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spu属性值
 * 
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
@Mapper
public interface ProductAttrValueDao extends BaseMapper<ProductAttrValueEntity> {

    void updateBySpuId(@Param("entity") ProductAttrValueEntity productAttrValueEntity);
    /**
     * mybatis不能在一个sql语句中执行多个更新操作
     */
//    void updateBatchBySpuId(List<ProductAttrValueEntity> attrValueEntityList);
}
