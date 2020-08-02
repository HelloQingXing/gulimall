/**
  * Copyright 2020 bejson.com 
  */
package com.qx.gulimall.product.entity.vo.spu;

import lombok.Data;

/**
 * Auto-generated: 2020-07-19 17:10:11
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class BaseAttrs {

    private Long attrId;
    private String attrValues;
    // 快速展示【是否展示在介绍上；0-否 1-是】
    private Integer showDesc;

}