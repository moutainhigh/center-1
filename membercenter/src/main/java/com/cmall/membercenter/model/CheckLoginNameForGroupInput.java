package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class CheckLoginNameForGroupInput extends RootInput{
	
	@ZapcomApi(value = "登录名", require = 1)
	private String loginName = "";

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
}
