package com.cmall.usercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiMemberRegisterInput extends RootInput {
	
	/**
	 * 注册账号
	 */
	@ZapcomApi(value="注册账号")
	private String userCode = "";

	/**
	 * 注册id
	 */
	@ZapcomApi(value="注册id")
	private String userId="";

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	
	
	
}
