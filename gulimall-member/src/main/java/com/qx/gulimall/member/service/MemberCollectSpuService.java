package com.qx.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qx.common.utils.PageUtils;
import com.qx.gulimall.member.entity.MemberCollectSpuEntity;

import java.util.Map;

/**
 * 会员收藏的商品
 *
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 19:59:42
 */
public interface MemberCollectSpuService extends IService<MemberCollectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

