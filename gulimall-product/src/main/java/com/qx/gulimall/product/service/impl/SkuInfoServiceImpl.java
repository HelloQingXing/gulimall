package com.qx.gulimall.product.service.impl;

import com.qx.gulimall.product.entity.*;
import com.qx.gulimall.product.entity.vo.sku.SkuItemVo;
import com.qx.gulimall.product.entity.vo.spu.Attr;
import com.qx.gulimall.product.feign.ESFeignService;
import com.qx.gulimall.product.feign.WareFeignService;
import com.qx.gulimall.product.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.product.dao.SkuInfoDao;
import org.springframework.util.StringUtils;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private AttrGroupService attrGroupService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private SpuInfoService spuInfoService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private ESFeignService esFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        // page=1&limit=10&key=&catelogId=0&brandId=0&min=0&max=0
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        // 组装条件
        Object key = params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and(w -> {
                w.like("sku_name",key);
            });
        }
        Object catelogId = params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId)){
            // id为0时，不设条件
            if(Integer.parseInt(catelogId.toString()) != 0){
                wrapper.eq("catalog_id",catelogId);
            }
        }
        Object brandId = params.get("brandId");
        if(!StringUtils.isEmpty(brandId)){
            // id为0时，不设条件
            if(Integer.parseInt(brandId.toString()) != 0){
                wrapper.eq("brand_id",brandId);
            }
        }
        Object min = params.get("min");
        if(!StringUtils.isEmpty(min)){
            wrapper.ge("price",min);
        }
        Object max = params.get("max");
        if(!StringUtils.isEmpty(max) && max.equals(0)){
            wrapper.le("price",max);
        }

        IPage<SkuInfoEntity> page =
                this.page(new Query<SkuInfoEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }


    @Override
    public List<SkuInfoEntity> getSkuInfoBySpuId(Long spuId) {

        // 设置条件
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id",spuId);

        return this.list(wrapper);
    }

    @Override
    public Boolean changeProductStatusBySpuId(Long spuId,Integer status) {

        Integer count = baseMapper.updateProductStatusBySpuId(spuId,status);
        return count > 0;
    }

    @Override
    public SkuItemVo itemPageBySkuId(Long skuId) {

        SkuItemVo skuItemVo = new SkuItemVo();
        // 获取sku基本信息
        CompletableFuture<SkuInfoEntity> skuInfoFuture = CompletableFuture.supplyAsync(() -> {
            SkuInfoEntity skuInfo = this.getById(skuId);
            skuItemVo.setSkuInfo(skuInfo);
            return skuInfo;
        }, executor);

        // 通过分类Id 获取所有属性
        /*QueryWrapper<AttrEntity> attrWrapper = new QueryWrapper<AttrEntity>().eq("catalog_id", catalogId);
        List<AttrEntity> attrs = attrService.list(attrWrapper);

        if(attrs != null && attrs.size() > 0){
            List<SkuItemVo.SaleAttr> saleAttrs = new ArrayList<>();
            List<SkuItemVo.AttrGroupVo> baseAttrs = new ArrayList<>();
            // 通过属性类型 筛选出基本属性和销售属性
            for (AttrEntity attr : attrs) {
                // 属性值-;号分隔：8GB+128GB;6GB+64GB;8GB+256GB
                String valueSelect = attr.getValueSelect();
                String[] splitVals = valueSelect.split(";");
                List<String> vals = Arrays.asList(splitVals).stream().collect(Collectors.toList());
                // 0-销售属性，1-基本属性
                Integer attrType = attr.getAttrType();
                if(attrType == 0){
                    SkuItemVo.SaleAttr saleAttr = new SkuItemVo.SaleAttr();
                    saleAttr.setAttrId(attr.getAttrId());
                    saleAttr.setAttrName(attr.getAttrName());
                    saleAttr.setAttrVals(vals);

                    saleAttrs.add(saleAttr);
                } else if (attrType == 1){


                }
            }
        }*/
        // 获取所有销售属性
        // 查询当前spu（spuId）下的所有sku（skuId）
        CompletableFuture<Void> saleAttrFuture = skuInfoFuture.thenAccept(skuInfo -> {
            List<Object> skuIds = this.listObjs(new QueryWrapper<SkuInfoEntity>()
                    .eq("spu_id", skuInfo.getSpuId()).select("sku_id"));
            List<SkuItemVo.SaleAttr> saleAttrs = skuSaleAttrValueService
                    .listSaleAttrBySkuIds(skuIds);

            skuItemVo.setSaleAttrs(saleAttrs);
        });

        CompletableFuture<Void> baseAttrFuture = skuInfoFuture.thenAccept(s -> {
            List<SkuItemVo.AttrGroupVo> attrGroupVos = new ArrayList<>();
            // 获取分组
            List<AttrGroupEntity> groupEntities = attrGroupService
                    .list(new QueryWrapper<AttrGroupEntity>()
                            .eq("catelog_id", s.getCatalogId()));
            // 获取分组&属性关联表
            for (AttrGroupEntity group : groupEntities) {
                // 基本属性
                SkuItemVo.AttrGroupVo groupAttr = new SkuItemVo.AttrGroupVo();
                // 设置属性名
                groupAttr.setAttrGroupName(group.getAttrGroupName());
                // 获取关联关系表
                List<AttrAttrgroupRelationEntity> relations = attrAttrgroupRelationService
                        .list(new QueryWrapper<AttrAttrgroupRelationEntity>()
                                .eq("attr_group_id", group.getAttrGroupId()));
                // 获取属性id
                List<Long> attrIds = relations.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
                // 通过属型id查询属性
                List<ProductAttrValueEntity> attrValueEntities = productAttrValueService.listByAttrIdsAndSpuId(attrIds,s.getSpuId());
                List<Attr> attrsRe = attrValueEntities.stream().map(attr -> {
                    Attr attr1 = new Attr();
                    attr1.setAttrId(attr.getAttrId());
                    attr1.setAttrName(attr.getAttrName());
                    attr1.setAttrValue(attr.getAttrValue());

                    return attr1;
                }).collect(Collectors.toList());
                groupAttr.setAttrs(attrsRe);
                attrGroupVos.add(groupAttr);
            }
            skuItemVo.setBaseAttrs(attrGroupVos);
        });

        // sku图片
        CompletableFuture<Void> imgFuture = skuInfoFuture.thenAcceptAsync((s) -> {
            List<SkuImagesEntity> skuImages = skuImagesService
                    .list(new QueryWrapper<SkuImagesEntity>()
                            .eq("sku_id", skuId));
            skuItemVo.setSkuImages(skuImages);
            // spu介绍
            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(s.getSpuId());
            skuItemVo.setSpuInfoDesc(spuInfoDescEntity);
        },executor);

        // 等待所有结果完成
        try {
            CompletableFuture.allOf(saleAttrFuture,baseAttrFuture,imgFuture).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return skuItemVo;
    }
}