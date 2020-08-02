package com.qx.gulimall.ware.service.impl;

import com.qx.common.constant.WareConstant;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.ware.dao.PurchaseDetailDao;
import com.qx.gulimall.ware.entity.PurchaseDetailEntity;
import com.qx.gulimall.ware.service.PurchaseDetailService;
import org.springframework.util.StringUtils;


@Service("purchaseDetailService")
public class PurchaseDetailServiceImpl extends ServiceImpl<PurchaseDetailDao, PurchaseDetailEntity> implements PurchaseDetailService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseDetailEntity> page = this.page(
                new Query<PurchaseDetailEntity>().getPage(params),
                new QueryWrapper<PurchaseDetailEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        // page=1&limit=10&key=&status=&wareId=
        QueryWrapper<PurchaseDetailEntity> wrapper = new QueryWrapper<>();
        Object key = params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w -> {
                w.eq("purchase_id",key)
                        .or().eq("sku_id",key)
                        .or().eq("sku_num",key)
                        .or().eq("sku_price",key);
            });
        }
        Object status = params.get("status");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("status",status);
        }
        Object wareId = params.get("wareId");
        if(!StringUtils.isEmpty(status)){
            wrapper.eq("ware_id",wareId);
        }

        IPage<PurchaseDetailEntity> page = this.page(new Query<PurchaseDetailEntity>().getPage(params), wrapper);
        PageUtils pageUtils = new PageUtils(page);

        return pageUtils;
    }

    /**
     * 设为采购状态
     * @param purchaseId
     */
    @Override
    public boolean updateStatusByPurchaseId(Long purchaseId) {

        List<PurchaseDetailEntity> purchaseDetailList = this
                .list(new QueryWrapper<PurchaseDetailEntity>().eq("purchase_id", purchaseId));
        List<PurchaseDetailEntity> purchaseDetailEntityList = purchaseDetailList.stream().filter(purchaseDetailEntity -> {
            // 当已经分配时，才执行采购
            if (purchaseDetailEntity.getStatus().equals(WareConstant.PurchaseDetailStatusEnum.ASSIGN.getCode())) {
                return true;
            }
            return false;
        }).map(purchaseDetailEntity -> {
            purchaseDetailEntity.setStatus(WareConstant.PurchaseDetailStatusEnum.BUYING.getCode());
            return purchaseDetailEntity;
        }).collect(Collectors.toList());
        // 执行更新
        boolean b = this.updateBatchById(purchaseDetailEntityList);

        return b;
    }

}