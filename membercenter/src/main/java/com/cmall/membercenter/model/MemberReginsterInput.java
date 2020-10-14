package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class MemberReginsterInput extends RootInput {
	@ZapcomApi(value = "手机号", require = 1, remark = "手机号", demo = "13388888888", verify = "base=mobile")
	private String mobile = "";

	@ZapcomApi(value = "用户密码", require = 1, demo = "123456", remark = "用户加密后的密码，长度为32位。", verify = {
			"minlength=32", "maxlength=32" })
	private String password = "";

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
