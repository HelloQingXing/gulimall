/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package com.qx.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.HttpStatus;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R /*<T>*/ extends HashMap<String, Object> implements Serializable {
	private static final long serialVersionUID = 1L;

	/*// 数据传输时，可以直接利用该泛型，避免每次都强转，继承了HashMap，如果数据未在map中，则数据在分布式调用时就会丢失数据
	private T data;
	public T getData() {
		return data;
	}
	// 设置数据,返回R可以直接链式编程
	public R setData(T data) {
		this.data = data;
		return this;
	}*/

	public R setData(Object o){
		this.put("data",o);
		return this;
	}

	public <T> T getData(TypeReference<T> typeReference){
		// 从map中获取数据
		Object data = this.get("data");
		// 将数据转为JSON字符串
		String s = JSON.toJSONString(data);
		// 将字符串转为所需要的类型
		T t = JSON.parseObject(s, typeReference);
		// 将数据返回
		return t;
	}

	public R() {
		put("code", 0);
		put("msg", "success");
	}
	
	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
	}
	
	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}
	
	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}
	
	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}
	
	public static R ok() {
		return new R();
	}

	@Override
	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}
	public  Integer getCode() {

		return (Integer) this.get("code");
	}

}
