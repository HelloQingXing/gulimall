package com.qx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qx.common.utils.PageUtils;
import com.qx.gulimall.product.entity.AttrEntity;
import com.qx.gulimall.product.entity.AttrGroupEntity;
import com.qx.gulimall.product.entity.vo.AttGroupWithAttrVo;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Long catId);

    /**
     * 获取指定分组关联的所有属性
     * @param attrgroupId
     * @return
     */
    List<AttrEntity> getRelationAttr(Long attrgroupId);

    PageUtils getNoattrRelation(Map<String, Object> params, Long attrgroupId);

    List<AttGroupWithAttrVo> getGroupWithAttr(Long catelogId);
}

