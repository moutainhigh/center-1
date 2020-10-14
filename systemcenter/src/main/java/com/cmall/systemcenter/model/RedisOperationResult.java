package com.cmall.systemcenter.model;

import com.srnpr.zapweb.webapi.RootResultWeb;

public class RedisOperationResult extends RootResultWeb{
	private boolean success;
	private String message;
	private String data;
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	
	
	
}
