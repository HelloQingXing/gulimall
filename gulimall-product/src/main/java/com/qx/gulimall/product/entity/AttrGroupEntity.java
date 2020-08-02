package com.qx.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 属性分组
 * 
 * @author qx
 * @email 1375257663@qq.com
 * @date 2020-07-06 16:57:26
 */
@Data
@TableName("pms_attr_group")
public class AttrGroupEntity implements Serializable {
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
	 * 所属分类id的完整路径，包括当前分类id，父id，父父id……
	 * 通过@TableField注解设置当前字段不存在，以免查询时带上该字段
	 */
	@TableField(exist = false)
	private Long[] catelogPath;

}
