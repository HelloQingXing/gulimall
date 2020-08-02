package com.qx.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qx.common.utils.PageUtils;
import com.qx.gulimall.product.entity.SpuInfoEntity;
import com.qx.gulimall.product.entity.vo.spu.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuVo(SpuSaveVo saveVo);

    PageUtils queryPageByCondition(Map<String, Object> params);

    boolean productUp(Long spuId);
}

