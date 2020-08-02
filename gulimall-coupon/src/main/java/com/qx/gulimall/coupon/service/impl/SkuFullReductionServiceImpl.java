package com.qx.gulimall.coupon.service.impl;

import com.qx.common.dto.MemberPrice;
import com.qx.common.dto.SkuReductionDto;
import com.qx.gulimall.coupon.entity.MemberPriceEntity;
import com.qx.gulimall.coupon.entity.SkuLadderEntity;
import com.qx.gulimall.coupon.service.MemberPriceService;
import com.qx.gulimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.coupon.dao.SkuFullReductionDao;
import com.qx.gulimall.coupon.entity.SkuFullReductionEntity;
import com.qx.gulimall.coupon.service.SkuFullReductionService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {

    @Autowired
    private SkuLadderService skuLadderService;
    @Autowired
    private MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveSkuReductionDto(SkuReductionDto skuReductionDto) {

        // 保存满减信息
        SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
        // 复制属性
        BeanUtils.copyProperties(skuReductionDto,skuFullReductionEntity);
        skuFullReductionEntity.setAddOther(skuReductionDto.getPriceStatus());
        // 满减大于0才执行保存
        if(skuFullReductionEntity.getFullPrice().intValue() > 0){
            // 执行保存
            this.save(skuFullReductionEntity);
        }


        // 保存基本信息
        SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
        // 复制属性
        BeanUtils.copyProperties(skuReductionDto,skuLadderEntity);
        skuLadderEntity.setAddOther(skuReductionDto.getPriceStatus());
        // 满足条件执行保存
        if(skuLadderEntity.getFullCount() >0 &&
                (skuLadderEntity.getDiscount().intValue() < 1 &&
                        skuLadderEntity.getDiscount().intValue() > 0)){
            // 保存
            skuLadderService.save(skuLadderEntity);
        }


        // 保存会员价格
        List<MemberPrice> memberPriceList = skuReductionDto.getMemberPrice();
        List<MemberPriceEntity> memberPriceEntityList = memberPriceList.stream().filter(memberPrice -> {

            return memberPrice.getPrice().intValue() > 0;
        }).map(memberPrice -> {
            // 复制属性
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            memberPriceEntity.setMemberLevelName(memberPrice.getName());
            memberPriceEntity.setAddOther(skuReductionDto.getPriceStatus());
            memberPriceEntity.setMemberPrice(memberPrice.getPrice());
            memberPriceEntity.setSkuId(skuReductionDto.getSkuId());
            memberPriceEntity.setId(memberPrice.getId());

            return memberPriceEntity;
        }).collect(Collectors.toList());

        memberPriceService.saveBatch(memberPriceEntityList);
    }

}