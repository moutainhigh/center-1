package com.cmall.productcenter.model;

import com.srnpr.zapcom.topapi.RootResult;

public class AppCategoryAddProductsResult extends RootResult {

	
	/**
	 * 属性列表
	 */
	private String code = "";
	private String name = "";
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
