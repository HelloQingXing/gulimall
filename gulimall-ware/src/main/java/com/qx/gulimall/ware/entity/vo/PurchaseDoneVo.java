package com.qx.gulimall.ware.entity.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @Classname PurchaseDoneVo
 * @Description 采购单完成返回项
 * @Date 2020/7/21 11:06
 * @Created by 卿星
 */
@Data
public class PurchaseDoneVo {

    /**
     * 采购单ID
     */
    @NotNull
    private Long id;
    /**
     * 采购项
     */
    @NotEmpty
    private List<PurchaseItemDoneVo> items;

}
