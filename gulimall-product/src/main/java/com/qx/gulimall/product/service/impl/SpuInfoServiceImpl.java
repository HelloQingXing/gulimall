package com.qx.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.qx.common.constant.ProductConstant;
import com.qx.common.dto.SkuMemberPriceDto;
import com.qx.common.dto.SkuReductionDto;
import com.qx.common.dto.SpuBoundDTo;
import com.qx.common.dto.StockVo;
import com.qx.common.dto.es.SkuESModel;
import com.qx.common.utils.R;
import com.qx.gulimall.product.entity.*;
import com.qx.gulimall.product.entity.vo.spu.*;
import com.qx.gulimall.product.feign.CouponService;
import com.qx.gulimall.product.feign.ESFeignService;
import com.qx.gulimall.product.feign.WareFeignService;
import com.qx.gulimall.product.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private AttrService attrService;
    @Autowired
    private SkuInfoService skuInfoService;
    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private SpuImagesService spuImagesService;
    @Autowired
    private SpuInfoDescService spuInfoDescService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private BrandService brandService;

    @Autowired
    private WareFeignService wareFeignService;
    @Autowired
    private ESFeignService esFeignService;
    @Autowired
    private CouponService couponService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSpuVo(SpuSaveVo saveVo) {

        // 保存商品基础信息pms_spu_info
        // 复制属性
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(saveVo,spuInfoEntity);
        // 设置上架状态
        spuInfoEntity.setPublishStatus(0);
        // 设置创建和更新时间
        spuInfoEntity.setCreateTime(new Date());
        spuInfoEntity.setUpdateTime(new Date());
        // 执行保存
        this.save(spuInfoEntity);

        // 获取保存后的spuId
        Long spuId = spuInfoEntity.getId();
        // 获取saveVo中国的brandId
        Long brandId = saveVo.getBrandId();
        // 获取catalogId
        Long catalogId = saveVo.getCatalogId();
        // 保存商品规格参数
        List<BaseAttrs> baseAttrsList = saveVo.getBaseAttrs();
        List<ProductAttrValueEntity> productAttrValueList = baseAttrsList.stream().map(baseAttrs -> {
            // 获取基本参数
            Long attrId = baseAttrs.getAttrId();
            String attrValues = baseAttrs.getAttrValues();
            Integer showDesc = baseAttrs.getShowDesc();

            // 创建实体类
            ProductAttrValueEntity attrValueEntity = new ProductAttrValueEntity();
            // 设置属性
            attrValueEntity.setSpuId(spuId);
            attrValueEntity.setAttrId(attrId);
            attrValueEntity.setAttrValue(attrValues);
            attrValueEntity.setQuickShow(showDesc);
            // 获取属性名
            AttrEntity id = attrService.getById(attrId);
            // 设置属性名
            attrValueEntity.setAttrName(id.getAttrName());

            return attrValueEntity;
        }).collect(Collectors.toList());

        // 保存
        productAttrValueService.saveBatch(productAttrValueList);

        // 保存SPU描述图片
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        // 将集合用字段分隔开
        String decript = String.join(",", saveVo.getDecript());
        System.out.println(decript);
        spuInfoDescEntity.setDecript(decript);
        spuInfoDescEntity.setSpuId(spuId);
        // 保存
        spuInfoDescService.save(spuInfoDescEntity);

        // 保存SPU图片集
        List<String> spuImageList = saveVo.getImages();
        // 创建集合封装数据
        List<SpuImagesEntity> spuImagesList = new ArrayList<>();
        spuImageList.forEach(image -> {
            SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
            // 设置属性
            spuImagesEntity.setImgUrl(image);
            spuImagesEntity.setSpuId(spuId);
            // 是否是默认图片
            /*if(image.getDefaultImg() == 1){
                spuImagesEntity.setDefaultImg(1);
            }*/
            // 添加到集合中
            spuImagesList.add(spuImagesEntity);
        });
        // 保存数据
        spuImagesService.saveBatch(spuImagesList);

        // 远程保存Bounds
        Bounds bounds = saveVo.getBounds();
        // 复制属性
        SpuBoundDTo boundDTo = new SpuBoundDTo();
        BeanUtils.copyProperties(bounds,boundDTo);
        boundDTo.setSpuId(spuId);
        // 执行保存
        R r1 = couponService.saveSpuBoundDTo(boundDTo);
        if(r1.getCode() != 0){
            log.error("积分信息未保存");
        }

        // 保存SkuInfo基本属性
        List<Skus> skusList = saveVo.getSkus();
        if(skusList != null && skusList.size() > 0){
            // 取出其中元素执行保存
            skusList.forEach(skus -> {

                // 保存SkuInfo
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                // 复制属性
                BeanUtils.copyProperties(skus,skuInfoEntity);
                // 设置属性
                skuInfoEntity.setBrandId(brandId);
                skuInfoEntity.setCatalogId(catalogId);
                skuInfoEntity.setSpuId(spuId);
                skuInfoEntity.setSaleCount(0L);

                // 获取默认图片
                List<Images> images = skus.getImages();
                images.forEach(image -> {
                    // 1：默认图片
                    if(image.getDefaultImg() == 1){
                        skuInfoEntity.setSkuDefaultImg(image.getImgUrl());
                    }
                });
                // 执行保存
                skuInfoService.save(skuInfoEntity);
                // 获取保存后的skuId
                Long skuId = skuInfoEntity.getSkuId();
                // 保存图片
                // 集合封装要保存的图片
                List<SkuImagesEntity> imagesSkuList = images.stream()
                        .filter(image -> {
                            // 过滤空图片
                            if(image.getImgUrl() == "" || image.getImgUrl() == null){
                                return false;
                            }
                            return true;
                        })
                        .map(image -> {
                            SkuImagesEntity skuImages = new SkuImagesEntity();
                            // 复制属性
                            BeanUtils.copyProperties(image, skuImages);
                            // 设置sku_id
                            skuImages.setSkuId(skuId);
                            // 默认图[0 - 不是默认图，1 - 是默认图]
                            if (image.getDefaultImg() == 1) {
                                skuImages.setDefaultImg(1);
                            }

                            return skuImages;
                        }).collect(Collectors.toList());
                // 批量保存
                skuImagesService.saveBatch(imagesSkuList);

                // sku销售属性 --》 pms_sku_sale_attr_value
                List<Attr> attrList = skus.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueList = new ArrayList<>();
                attrList.forEach(attr -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    // 复制属性
                    BeanUtils.copyProperties(attr,skuSaleAttrValueEntity);
                    // 获取添加SKU后生成的商品SKU_ID
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    // 添加到集合中统一保存
                    skuSaleAttrValueList.add(skuSaleAttrValueEntity);
                });
                // 保存
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueList);

                // feign远程保存sms_member_price

                List<MemberPrice> memberPriceList = skus.getMemberPrice();
                List<SkuMemberPriceDto> memberPriceDtoList = memberPriceList.stream().map(memberPrice -> {
                    // 创建数据传输对象
                    SkuMemberPriceDto skuMemberPriceDto = new SkuMemberPriceDto();
                    // 复制属性
                    BeanUtils.copyProperties(memberPrice, skuMemberPriceDto);
                    // 设置skuId
                    skuMemberPriceDto.setSkuId(skuId);
//                skuMemberPriceDto.set
                    return skuMemberPriceDto;
                }).collect(Collectors.toList());

                couponService.saveMemberPriceDtoList(memberPriceDtoList);

                // 远程保存优惠满减信息
                SkuReductionDto skuReductionDto = new SkuReductionDto();
                BeanUtils.copyProperties(skus,skuReductionDto);
                skuReductionDto.setSkuId(skuId);

                if(skuReductionDto.getFullCount() > 0 || skuReductionDto.getDiscount().intValue() > 0){
                    R r = couponService.saveSkuReductionDto(skuReductionDto);
                    if(r.getCode() != 0){
                        log.error("优惠满减信息未保存");
                    }
                }

            });
        }



    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {

        // 封装条件 status=&key=&brandId=0&catelogId=225&page=1&limit=10
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        Object status = params.get("status");
        if (!StringUtils.isEmpty(status)){
            wrapper.eq("publish_status",status);
        }
        Object key = params.get("key");
        if (!StringUtils.isEmpty(key)){
            wrapper.and(w -> {
                w.like("spu_name",key);
            });
        }
        Object brandId = params.get("brandId");
        if (!StringUtils.isEmpty(brandId)){
            // id为0时，不设条件
            if(Integer.parseInt(brandId.toString()) != 0){
                wrapper.eq("brand_id",brandId);
            }
        }
        Object catelogId = params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId)){
            // id为0时，不设条件
            if(Integer.parseInt(catelogId.toString()) != 0){
                wrapper.eq("catalog_id",catelogId);
            }

        }

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(params), wrapper);

        PageUtils pageUtils = new PageUtils(page);

        return pageUtils;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean productUp(Long spuId) {

        // attrs
        // 通过spuId查询属性Attr
        List<ProductAttrValueEntity> attrValueList = attrService.listforspuBySpuId(spuId);
        if(attrValueList != null && attrValueList.size() > 0){
            List<Long> attrIds = attrValueList.stream().map(attr -> {
                // 返回需要的ID
                return attr.getAttrId();
            }).collect(Collectors.toList());
            // 通过attrIds查询所有能被检索的id集合
            List<Long> atrIdSearch = attrService.queryAttrIdsIsSearch(attrIds);
            // 将集合放入set中便于比较
            Set<Long> idSet = new HashSet<>(atrIdSearch);
            // 过滤不能够检索,并返回需要的属性
            List<SkuESModel.Attr> skuAttrList = attrValueList.stream().filter(attr -> {
                return idSet.contains(attr.getAttrId());
            }).map(attr -> {
                // 复制属性
                SkuESModel.Attr skuAttr = new SkuESModel.Attr();
                BeanUtils.copyProperties(attr, skuAttr);
                return skuAttr;
            }).collect(Collectors.toList());

            // 通过spuId查出对应的所有sku信息
            List<SkuInfoEntity> skuInfoList = skuInfoService.getSkuInfoBySpuId(spuId);

            // 通过所有skuId查询所有库存  SkuInfoEntity::getSkuId简写 lambda简写形式
            List<Long> skuIdList = skuInfoList.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
            // 远程调用查询库存
            Map<Long, Boolean> stockVoMap = null;
            try{
                R hasStockBySkuIds = wareFeignService.hasStockBySkuIds(skuIdList);
                TypeReference<List<StockVo>> listTypeReference = new TypeReference<List<StockVo>>(){};
                List<StockVo> skuIdsData = hasStockBySkuIds.getData(listTypeReference);
                if(skuIdsData != null){
                    stockVoMap = skuIdsData.stream()
                            .map(item -> item)
                            .collect(Collectors.toMap(StockVo::getSkuId, stockVo -> stockVo.getStock() > 0));
                }
            }catch (Exception e){
                log.error("库存检查失败：{}" + e.getMessage());
            }

            Map<Long, Boolean> finalStockVoMap = stockVoMap;
            List<SkuESModel> skuESModelList = skuInfoList.stream().map(skuInfoEntity -> {
                // 创建SkuEs接收对象
                SkuESModel skuESModel = new SkuESModel();
                // 复制相同属性
                BeanUtils.copyProperties(skuInfoEntity, skuESModel);
                // skuImg skuPrice
                skuESModel.setSkuImg(skuInfoEntity.getSkuDefaultImg());
                skuESModel.setSkuPrice(skuInfoEntity.getPrice());

                // 设置相关性得分
                skuESModel.setHotScore(0.0);
                // catalogName
                // 通过catalogId调用categoryService获得CategoryEntity
                Long catalogId = skuInfoEntity.getCatalogId();
                CategoryEntity byId = categoryService.getById(catalogId);
                // 设置CatalogName
                skuESModel.setCatalogName(byId.getName());
                //   brandName  brandImg
                BrandEntity brandEntity = brandService.getById(skuInfoEntity.getBrandId());
                skuESModel.setBrandName(brandEntity.getName());
                skuESModel.setBrandImg(brandEntity.getLogo());

                // 设置能够检索的属性
                skuESModel.setAttrs(skuAttrList);
                // 设置库存
                if(finalStockVoMap != null && finalStockVoMap.size() > 0){
                    Boolean flag = finalStockVoMap.get(skuInfoEntity.getSkuId());
                    skuESModel.setHasStock(flag);
                } else {
                    skuESModel.setHasStock(true);
                }

                return skuESModel;
            }).collect(Collectors.toList());

            // 调用ES保存上架数据
            R r = esFeignService.upProduct(skuESModelList);

            if(r.getCode() != 0){
                log.error("商品上架失败");
                // TODO 重复调用，接口幂等性
            } else {
                // 修改商品上架状态
                skuInfoService.changeProductStatusBySpuId(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
                return true;
            }

        }
        return false;

    }

}