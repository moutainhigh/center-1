package com.cmall.membercenter.group.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class GroupLoginInput extends RootInput {

	@ZapcomApi(value = "用户名", remark = "用户名", demo = "13512345678", require = 1, verify = "base=mobile")
	private String loginName = "";

	@ZapcomApi(value = "用户密码", require = 1, demo = "123456", remark = "用户的密码，长度为6-40位，支持特殊字符。", verify = {
			"minlength=6", "maxlength=40" })
	private String loginPass = "";
	
	@ZapcomApi(value = "流水号", require = 0, remark = "流水号app传递过来", demo = "234324654575")
	private String serialNumber = "";

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

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
