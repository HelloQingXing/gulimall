package com.qx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qx.common.dto.es.SkuESModel;
import com.qx.common.utils.PageUtils;
import com.qx.gulimall.product.entity.AttrEntity;
import com.qx.gulimall.product.entity.ProductAttrValueEntity;
import com.qx.gulimall.product.entity.vo.AttrResVo;
import com.qx.gulimall.product.entity.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryBasePage(Map<String, Object> params, Long catelogId);

    void saveAttrVo(AttrVo attr);

    AttrResVo getInfoById(Long attrId);

    PageUtils querySalePage(Map<String, Object> params, Long catelogId);

    PageUtils queryTypePage(Map<String, Object> params, Long catelogId, String type);

    void updateAttr(AttrResVo attrResVo);

    List<ProductAttrValueEntity> listforspuBySpuId(Long spuId);

    List<SkuESModel.Attr> getSkuAttrByCatalogId(Long catalogId);

    /**
     * 查询并返回attrIds中能被检索的attrId
     * @param attrIds
     * @return
     */
    List<Long> queryAttrIdsIsSearch(List<Long> attrIds);
}

