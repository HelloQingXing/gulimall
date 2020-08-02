package com.qx.gulimall.ware.service.impl;

import com.qx.common.constant.WareConstant;
import com.qx.common.utils.R;
import com.qx.gulimall.ware.entity.PurchaseDetailEntity;
import com.qx.gulimall.ware.entity.WareSkuEntity;
import com.qx.gulimall.ware.entity.vo.MergeVo;
import com.qx.gulimall.ware.entity.vo.PurchaseDoneVo;
import com.qx.gulimall.ware.entity.vo.PurchaseItemDoneVo;
import com.qx.gulimall.ware.feign.ProductFeignService;
import com.qx.gulimall.ware.service.PurchaseDetailService;
import com.qx.gulimall.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.ware.dao.PurchaseDao;
import com.qx.gulimall.ware.entity.PurchaseEntity;
import com.qx.gulimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {

    @Autowired
    private PurchaseDetailService purchaseDetailService;
    @Autowired
    private WareSkuService wareSkuService;
    @Autowired
    private ProductFeignService productFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryUnreceiveListPage(Map<String, Object> params) {

        QueryWrapper<PurchaseEntity> wrapper = new QueryWrapper<>();
        // 根据status查询 0:刚新建，1：分配给某人还未领取采购单
        wrapper.eq("status",0).or().eq("status",1);
        IPage<PurchaseEntity> page = this.page(new Query<PurchaseEntity>().getPage(params), wrapper);

        PageUtils pageUtils = new PageUtils(page);
        return pageUtils;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void mergePurchase(MergeVo mergeVo) {

        // 采购单ID，为null表示未分配
        Long purchaseId = mergeVo.getPurchaseId();
        if(purchaseId  == null  || StringUtils.isEmpty(mergeVo.getPurchaseId())){
            PurchaseEntity purchase = new PurchaseEntity();
            // 设置时间
            purchase.setCreateTime(new Date());
            purchase.setUpdateTime(new Date());
            purchase.setStatus(WareConstant.PurchaseStatusEnum.CREATED.getCode());
            // 保存
            this.save(purchase);
            // 获取保存ID
            purchaseId = purchase.getId();
        }
        List<Long> items = mergeVo.getItems();
        // 采购单ID必须有效
        Long finalPurchaseId = purchaseId;
        List<PurchaseDetailEntity> purchaseDetailList = items.stream()
        // 采购单必须为新建，或者已分配才行
        .filter(entity -> {
            // 如果前台未传id则直接放行
            if(mergeVo.getPurchaseId() == null || StringUtils.isEmpty(mergeVo.getPurchaseId())){
                return true;
            } else{
                // 调用查询
                PurchaseEntity sku_id = this.getById(entity);
                if(sku_id.getStatus().equals(WareConstant.PurchaseStatusEnum.CREATED.getCode()) ||
                        sku_id.getStatus().equals(WareConstant.PurchaseStatusEnum.ASSIGN.getCode())){
                    return true;
                }
                return false;
            }

        })
        .map(entity -> {
            // 设置采购单详情
            PurchaseDetailEntity purchaseDetailEntity = new PurchaseDetailEntity();
            purchaseDetailEntity.setId(entity);
            purchaseDetailEntity.setPurchaseId(finalPurchaseId);
            purchaseDetailEntity.setStatus(WareConstant.PurchaseStatusEnum.ASSIGN.getCode());

            return purchaseDetailEntity;
        }).collect(Collectors.toList());

        // 执行保存
        purchaseDetailService.updateBatchById(purchaseDetailList);

        // 采购项必须已分配或新建才行
        PurchaseEntity byId = this.getById(purchaseId);
        System.out.println(byId.getStatus());
        System.out.println(byId.getStatus().equals(WareConstant.PurchaseStatusEnum.CREATED.getCode()));
//        if(byId.getStatus().equals(byId.getStatus().equals(WareConstant.PurchaseStatusEnum.CREATED.getCode()) || 多加了一个equals
        if(byId.getStatus().equals(WareConstant.PurchaseStatusEnum.CREATED.getCode()) ||
                byId.getStatus().equals(WareConstant.PurchaseStatusEnum.ASSIGN.getCode())){
            // 设置采购时间
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.ASSIGN.getCode());
            purchaseEntity.setId(purchaseId);

            this.updateById(purchaseEntity);
        }

    }

    /**
     * 完成采购需求
     * @param ids
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void receivedPurchase(List<Long> ids) {

        // 判断当前采购单ID是否未被采购
        List<PurchaseEntity> purchaseEntityList = ids.stream().map(id -> {
            // 执行采购，并返回状态
            boolean flag = purchaseDetailService.updateStatusByPurchaseId(id);
            PurchaseEntity purchaseEntity = new PurchaseEntity();
            // 为true表示当前采购项可以被采购
            if(flag){
                purchaseEntity.setId(id);
                purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.RECEIVED.getCode());
            }

            return purchaseEntity;
        }).collect(Collectors.toList());

        this.updateBatchById(purchaseEntityList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void donePurchase(PurchaseDoneVo purchaseDoneVo) {

        Long purchaseId = purchaseDoneVo.getId();

        // 完成采购项
        // 定义采购状态和接收采购项的集合
        boolean flag = true;
        List<PurchaseDetailEntity> purchaseDetailEntityList = new ArrayList<>();
        // 获取数据并遍历
        List<PurchaseItemDoneVo> doneVoItems = purchaseDoneVo.getItems();
        for (PurchaseItemDoneVo item : doneVoItems) {
            PurchaseDetailEntity detailEntity = new PurchaseDetailEntity();
            // 设置采购数据
            Long id = item.getItemId();
            Integer status = item.getStatus();
            // 有异常时，将采购单状态设置为异常
            if (status.equals(WareConstant.PurchaseDetailStatusEnum.HASERROR.getCode())) {
                flag = false;
            } else{
                // 查询当前采购项
                PurchaseDetailEntity entity = purchaseDetailService.getById(id);
                // 更新库存
                WareSkuEntity wareSkuEntity = new WareSkuEntity();
                // 设置参数
                wareSkuEntity.setWareId(entity.getWareId());
                wareSkuEntity.setSkuId(entity.getSkuId());
                wareSkuEntity.setStock(entity.getSkuNum());
                wareSkuEntity.setStockLocked(0);
                // 远程调用获取skuName，使用try-catch避免冗余字段设置失败后产生回滚
                try{
                    R info = productFeignService.info(entity.getSkuId());
                    if(info.getCode() == 0){
                        Map<String,Object> skuInfo = (Map<String, Object>) info.get("skuInfo");
                        String skuName = (String) skuInfo.get("skuName");

                        wareSkuEntity.setSkuName(skuName);
                    }
                }catch (Exception e){
                    log.error(e.getMessage());
                }
                // 执行保存
                wareSkuService.addStock(wareSkuEntity);

            }
            // 更新详情
            detailEntity.setStatus(status);
            detailEntity.setId(id);
            purchaseDetailEntityList.add(detailEntity);

        }
        // 批量修改
        purchaseDetailService.updateBatchById(purchaseDetailEntityList);

        PurchaseEntity purchaseEntity = new PurchaseEntity();
        purchaseEntity.setId(purchaseId);
        // 判断采购单中的采购项是否有异常
        if(!flag){
            // 设置异常状态
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.HASERROR.getCode());
        } else {
            purchaseEntity.setStatus(WareConstant.PurchaseStatusEnum.FINISH.getCode());
        }

        this.updateById(purchaseEntity);
    }

}