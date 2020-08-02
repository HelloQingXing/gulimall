package com.qx.gulimall.ware.service.impl;

import com.qx.common.dto.StockVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.ware.dao.WareSkuDao;
import com.qx.gulimall.ware.entity.WareSkuEntity;
import com.qx.gulimall.ware.service.WareSkuService;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // 封装条件 page=1&limit=10&skuId=&wareId=
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String skuId = (String) params.get("skuId");
        if(!StringUtils.isEmpty(skuId)){
            wrapper.eq("sku_id",skuId);
        }
        String wareId = (String) params.get("wareId");
        if(!StringUtils.isEmpty(wareId)){
            wrapper.eq("ware_id",wareId);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),wrapper);

        return new PageUtils(page);
    }

    @Override
    public void addStock(WareSkuEntity wareSkuEntity) {

        Long skuId = wareSkuEntity.getSkuId();
        Long wareId = wareSkuEntity.getWareId();
        Integer stock = wareSkuEntity.getStock();
        // 查询判断当前仓库是否还有该商品库存
        List<WareSkuEntity> wareSkuEntityList = this.list(
                new QueryWrapper<WareSkuEntity>()
                        .eq("sku_id", skuId)
                        .eq("ware_id", wareId));
        // 不存在则新增，存在则只改库存
        if(wareSkuEntityList != null && wareSkuEntityList.size() > 0){
            baseMapper.updateStock(skuId,wareId,stock);
        } else{
            baseMapper.insert(wareSkuEntity);
        }

    }

    @Override
    public boolean hasStock(Long skuId) {

        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId);

        return this.count(wrapper) > 0;
    }

    @Override
    public List<StockVo> hasStockBySkuIds(List<Long> skuIdList) {

        List<StockVo> stockVoList = skuIdList.stream().map(skuId -> {
            return baseMapper.selectStockBySkuId(skuId);
        }).filter(stockVo -> {
            return stockVo != null;
        }).collect(Collectors.toList());

        return stockVoList;
    }

}