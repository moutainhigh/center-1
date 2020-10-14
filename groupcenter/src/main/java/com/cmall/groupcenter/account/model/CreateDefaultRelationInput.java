package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;


public class CreateDefaultRelationInput extends RootInput {

	@ZapcomApi(value = "手机号", demo = "13512345678", require = 1, verify = "base=mobile")
	String loginName="";
	
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

}
