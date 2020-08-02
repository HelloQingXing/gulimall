package com.qx.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qx.common.dto.SkuMemberPriceDto;
import com.qx.common.utils.PageUtils;
import com.qx.gulimall.coupon.entity.MemberPriceEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品会员价格
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 19:54:16
 */
public interface MemberPriceService extends IService<MemberPriceEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveMemberPriceDtoList(List<SkuMemberPriceDto> memberPriceDtoList);

}

