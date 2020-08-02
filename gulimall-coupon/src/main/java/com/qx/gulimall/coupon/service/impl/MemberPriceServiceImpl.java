package com.qx.gulimall.coupon.service.impl;

import com.qx.common.dto.SkuMemberPriceDto;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.coupon.dao.MemberPriceDao;
import com.qx.gulimall.coupon.entity.MemberPriceEntity;
import com.qx.gulimall.coupon.service.MemberPriceService;


@Service("memberPriceService")
public class MemberPriceServiceImpl extends ServiceImpl<MemberPriceDao, MemberPriceEntity> implements MemberPriceService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberPriceEntity> page = this.page(
                new Query<MemberPriceEntity>().getPage(params),
                new QueryWrapper<MemberPriceEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveMemberPriceDtoList(List<SkuMemberPriceDto> memberPriceDtoList) {

        // 创建集合接收
        ArrayList<MemberPriceEntity> memberPriceList = new ArrayList<>();
        // 遍历
        List<MemberPriceEntity> collect = memberPriceDtoList.stream().map(memberPriceDto -> {
            // 复制属性
            MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
            BeanUtils.copyProperties(memberPriceDto, memberPriceEntity);
            // 放入集合
            memberPriceList.add(memberPriceEntity);
            return memberPriceEntity;
        }).collect(Collectors.toList());
        // 执行保存
        this.saveBatch(collect);

    }

}