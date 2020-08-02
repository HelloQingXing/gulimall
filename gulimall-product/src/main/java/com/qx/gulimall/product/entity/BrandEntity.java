package com.qx.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.qx.common.valid.AddGroup;
import com.qx.common.valid.ListValue;
import com.qx.common.valid.UpdateGroup;
import com.qx.common.valid.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * 品牌
 * 
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@TableId
	@Null(message = "品牌id自增，无需携带",groups = {AddGroup.class})
	@NotNull(message = "品牌id不能为空",groups = {UpdateGroup.class})
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名必须非空",groups = {AddGroup.class,UpdateGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank
	@URL(message = "品牌logo地址不合法",groups = {AddGroup.class,UpdateGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	@NotBlank(message = "介绍必须填写",groups = {AddGroup.class,UpdateGroup.class})
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 * 必须指定value = {0,1}
	 */
	@NotNull(groups = {AddGroup.class, UpdateStatusGroup.class,UpdateGroup.class})
	@ListValue(value = {0,1},groups = {AddGroup.class, UpdateStatusGroup.class,UpdateGroup.class})
	private Integer showStatus;
	/**
	 * 检索首字母
	 *
	 */
//	@Pattern(regexp = "/^[A-z]$/",groups = {AddGroup.class,UpdateGroup.class}) // 正则表达式在java中不能带 /……/
	@Pattern(regexp = "^[A-z]$",groups = {AddGroup.class,UpdateGroup.class})
	private String firstLetter;
	/**
	 * 排序
	 */
	@Min(value = 0,message = "排序字段必须是大于等于0的整数",groups = {AddGroup.class,UpdateGroup.class})
	private Integer sort;

}
