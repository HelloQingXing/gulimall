package com.qx.gulimall.ware.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * @Classname MergeVo
 * @Description 采购需求
 * @Date 2020/7/20 22:32
 * @Created by 卿星
 */
@Data
public class MergeVo {

    /**
     * 采购单ID
     */
    private Long purchaseId;
    /**
     * 采购项
     */
    private List<Long> items;

}
