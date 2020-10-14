package com.cmall.systemcenter.model;

import com.srnpr.zapcom.topapi.RootInput;

public class RedisOperationInput extends RootInput{
	private String operation;//操作类型 get  set
	private String type;//value值类型
	private String key;//键
	private String value;//值
	private Integer time;//过期时间
	private String preKey;
	
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Integer getTime() {
		return time;
	}
	public void setTime(Integer time) {
		this.time = time;
	}
	public String getPreKey() {
		return preKey;
	}
	public void setPreKey(String preKey) {
		this.preKey = preKey;
	}
	
	
}
