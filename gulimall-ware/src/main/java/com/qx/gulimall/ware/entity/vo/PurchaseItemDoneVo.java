package com.qx.gulimall.ware.entity.vo;

import lombok.Data;

/**
 * @Classname PurchaseItemDoneVo
 * @Description 采购单完成项
 * @Date 2020/7/21 11:08
 * @Created by 卿星
 */
@Data
public class PurchaseItemDoneVo {

    // itemId:1,status:4,reason
    private Long itemId; // 当前采购ID
    private Integer status; // 采购状态
    private String reason;  // 采购原因

}
