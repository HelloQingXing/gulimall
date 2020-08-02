package com.qx.gulimall.product.entity.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.qx.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Classname AttGroupWithAttrVo
 * @Description 所有分组&关联属性
 * @Date 2020/7/19 10:05
 * @Created by 卿星
 */
@Data
public class AttGroupWithAttrVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分组id
     */
    @TableId
    private Long attrGroupId;
    /**
     * 组名
     */
    private String attrGroupName;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 描述
     */
    private String descript;
    /**
     * 组图标
     */
    private String icon;
    /**
     * 所属分类id
     */
    private Long catelogId;

    /**
     * 属性集合
     */
    private List<AttrEntity> attrs;

}
