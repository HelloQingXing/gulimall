package com.qx.gulimall.ware.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qx.common.utils.PageUtils;
import com.qx.common.utils.Query;

import com.qx.gulimall.ware.dao.WareInfoDao;
import com.qx.gulimall.ware.entity.WareInfoEntity;
import com.qx.gulimall.ware.service.WareInfoService;
import org.springframework.util.StringUtils;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        // page=1&limit=10&key=
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        Object key = params.get("key");
        if(StringUtils.isEmpty(key) && key != null){
            wrapper.and(w -> {
                w.like("name",key).or().like("address",key).or().like("areacode",key);
            });
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),wrapper);

        return new PageUtils(page);
    }

}