package com.qx.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.qx.gulimall.product.entity.vo.sku.SkuItemVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.product.dao.SkuSaleAttrValueDao;
import com.qx.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.qx.gulimall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemVo.SaleAttr> listSaleAttrBySkuIds(List<Object> skuIds) {

        /*QueryWrapper<SkuSaleAttrValueEntity> wrapper = new QueryWrapper<SkuSaleAttrValueEntity>()
                .in("sku_id", skuIds)
                // attr_value：不在groupBy中，所以必须处理
                // DISTINCT ：去重 GROUP_CONCAT：把相同属性连接起来 groupBy ：按指定属性分组
                .select("attr_id", "attr_name", "attr_value","GROUP_CONCAT(sku_id) skuIds")
                .groupBy("attr_id","attr_name");
        List<SkuSaleAttrValueEntity> skuSaleAttrs = baseMapper.selectList(wrapper);*/
        List<SkuItemVo.SaleAttr> saleAttrs = baseMapper.listSaleAttrBySkuIds(skuIds);
        System.out.println(saleAttrs);
        // 使用set使元素添加时不重复
//        HashSet<SkuItemVo.SaleAttr> saleAttrs1 = new HashSet<>();
        // 遍历组装元素
        /*List<SkuItemVo.SaleAttr> saleAttrs = null;
        if(skuSaleAttrs != null && skuSaleAttrs.size() > 0){
            saleAttrs = skuSaleAttrs.stream().map(attr -> {
                SkuItemVo.SaleAttr saleAttr = new SkuItemVo.SaleAttr();
                saleAttr.setAttrId(attr.getAttrId());
                saleAttr.setAttrName(attr.getAttrName());
                // value以逗号形式分隔：6GB+64GB,8GB+128GB,8GB+256GB
                String[] vals = attr.getAttrValue().split(",");
                // 转为集合
                List<String> vs = Arrays.asList(vals);
                saleAttr.setAttrVals(vs);

                return saleAttr;
            }).collect(Collectors.toList());
        }*/
        /*for (int i = 0;i < skuSaleAttrs.size();i++) {
            SkuSaleAttrValueEntity s = skuSaleAttrs.get(i);
            // 如果为第一个元素，则新建并添加，避免进入不了for循环
            if(i == 0){
                SkuItemVo.SaleAttr sa = new SkuItemVo.SaleAttr();
                sa.setAttrId(s.getAttrId());
                sa.setAttrName(s.getAttrName());
                sa.setAttrVals(Arrays.asList(s.getAttrValue()));
                saleAttrs.add(sa);
            } else {
                // 判断销售属性中是否有该属性，有就新添加属性
                for (SkuItemVo.SaleAttr saleAttr : saleAttrs) {
                    SkuItemVo.SaleAttr sa = new SkuItemVo.SaleAttr();
                    // 如果有属性则添加，没有则新建添加
                    if(saleAttr.getAttrId().equals(s.getAttrId())){
                        if(saleAttr.getAttrName().equals(s.getAttrName())){
                              // 添加到saleAttr中
                              // java.lang.UnsupportedOperationException: null AbstractList：抽象List不支持添加 remove，add等 method在AbstractList中是默认throw UnsupportedOperationException而且不作任何操作。
                              //	at java.util.AbstractList.add(AbstractList.java:148) ~[na:1.8.0_131]
                            List<String> attrVals = saleAttr.getAttrVals();
                            // 只有转换为ArrayList后才能执行此操作
                            attrVals = new ArrayList<>(attrVals);
                            attrVals.add(s.getAttrValue());
                            saleAttr.setAttrVals(attrVals);
                            break;
                        } else {
                            // 如果属性名不同，则新建一个并保存
                            sa.setAttrId(saleAttr.getAttrId());
                            sa.setAttrName(s.getAttrName());
                            sa.setAttrVals(Arrays.asList(s.getAttrValue()));
                            saleAttrs.add(sa);
                            // 停止，避免添加后再次遍历
                            break;
                        }
                    } else {
                        // 如果属性id没有则表示是还未添加的
                        sa.setAttrId(s.getAttrId());
                        sa.setAttrName(s.getAttrName());
                        sa.setAttrVals(Arrays.asList(s.getAttrValue()));
                        saleAttrs.add(sa);
                        // 停止，避免添加后再次遍历
                        break;
                    }

                }
            }

        }*/

        return saleAttrs;
    }

}