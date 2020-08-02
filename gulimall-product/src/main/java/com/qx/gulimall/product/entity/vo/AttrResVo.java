package com.qx.gulimall.product.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @Classname AttrResVo
 * @Description 页面响应数据
 * @Date 2020/7/15 11:40
 * @Created by 卿星
 */
@Data
public class AttrResVo extends AttrVo {
    /**
     * 分类名
     */
    private String catelogName;
    /**
     * 分组名
     */
    private String attrGroupName;

    /**
     * 多重分组
     */
    private List<String> attrGroupNameList;

}
