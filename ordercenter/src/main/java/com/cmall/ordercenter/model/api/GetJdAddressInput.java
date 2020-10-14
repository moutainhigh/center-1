package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.topapi.RootInput;

public class GetJdAddressInput extends RootInput {
	
	/** 父级编号，为空则查询全部第一级 */
	private String code;
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
}
