package com.cmall.groupcenter.oauth.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class SetParentMemberInput extends RootInput {

	
	@ZapcomApi(value = "用户授权码",demo="123456",require=1,remark="用户授权码")
	private String accessToken = "";

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	
	@ZapcomApi(value = "上级手机号", demo = "13512345678", require = 1, verify = "base=mobile")
	String parentLoginName="";

	public String getParentLoginName() {
		return parentLoginName;
	}

	public void setParentLoginName(String parentLoginName) {
		this.parentLoginName = parentLoginName;
	}
	
}
