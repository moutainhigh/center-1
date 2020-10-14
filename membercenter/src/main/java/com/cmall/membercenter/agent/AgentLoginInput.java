package com.cmall.membercenter.agent;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class AgentLoginInput extends RootInput {

	@ZapcomApi(value = "用户名", remark = "用户名", demo = "13512345678", require = 1)
	private String loginName = "";

	@ZapcomApi(value = "用户密码", require = 1, demo = "123456", remark = "用户的密码，长度为6-40位，支持特殊字符。", verify = {
			"minlength=6", "maxlength=40" })
	private String loginPass = "";
	
	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPass() {
		return loginPass;
	}

	public void setLoginPass(String loginPass) {
		this.loginPass = loginPass;
	}

}
