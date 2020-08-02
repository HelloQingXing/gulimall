package com.qx.gulimall.product.entity.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @Classname AttrVo
 * @Description 视图传输对象
 * @Date 2020/7/14 20:41
 * @Created by 卿星
 */

@Data
public class AttrVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 属性id
     */
    private Long attrId;
    /**
     * 属性名
     */
    private String attrName;
    /**
     * 是否需要检索[0-不需要，1-需要]
     */
    private Integer searchType;
    /**
     * 属性图标
     */
    private String icon;
    /**
     * 可选值列表[用逗号分隔]
     */
    private String valueSelect;
    /**
     * 属性类型[0-销售属性，1-基本属性，2-既是销售属性又是基本属性]
     */
    private Integer attrType;
    /**
     * 启用状态[0 - 禁用，1 - 启用]
     */
    private Long enable;
    /**
     * 所属分类
     */
    private Long catelogId;
    /**
     * 快速展示【是否展示在介绍上；0-否 1-是】，在sku中仍然可以调整
     */
    private Integer showDesc;

    /**
     * 是否多选 0-单选，1-多选
     */
    private Integer valueType;

    /**
     * 分组ID
     */
    private Long attrGroupId;

    /**
     * 三级路径
     */
    private Long[] catelogPath;

}
