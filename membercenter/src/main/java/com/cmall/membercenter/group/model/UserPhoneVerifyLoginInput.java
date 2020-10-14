package com.cmall.membercenter.group.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class UserPhoneVerifyLoginInput extends RootInput {

	@ZapcomApi(value = "用户名", remark = "用户名", demo = "13512345678", require = 1, verify = "base=mobile")
	private String loginName = "";

	@ZapcomApi(value = "验证码", require = 1, remark = "验证码")
	private String verifyCode = "";
	
	@ZapcomApi(value = "流水号", require = 0, remark = "流水号app传递过来", demo = "234324654575")
	private String serialNumber = "";
	
	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	

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

	
}
