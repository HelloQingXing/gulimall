package com.qx.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qx.common.utils.PageUtils;
import com.qx.gulimall.ware.entity.PurchaseEntity;
import com.qx.gulimall.ware.entity.vo.MergeVo;
import com.qx.gulimall.ware.entity.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 20:10:59
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询未领取的采购单
     * @return
     */
    PageUtils queryUnreceiveListPage(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);

    void receivedPurchase(List<Long> ids);

    void donePurchase(PurchaseDoneVo purchaseDoneVo);
}

