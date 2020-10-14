package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class RecommendUserRegisterInput extends RootInput{
	
	@ZapcomApi(value = "验证码", demo = "123456", require = 1, remark = "手机APP注册时为手机收到的短信验证码")
	private String verifyCode = "";

	@ZapcomApi(value = "手机号", require = 1, remark = "手机号", demo = "13388888888", verify = "base=mobile")
	private String mobile = "";

	@ZapcomApi(value = "用户密码", require = 0, demo = "123456", remark = "用户的密码，长度为6-30位，支持特殊字符。", verify = {
			"minlength=6", "maxlength=30" })
	private String password = "";
	
	@ZapcomApi(value = "流水号", require = 0, remark = "流水号app传递过来", demo = "234324654575")
	private String serialNumber = "";

	@ZapcomApi(value = "推荐人手机号", require = 0, remark = "第三方注册选填推荐人手机号", demo = "13388888888")
	private String referrerMobile = "";
	
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

	public String getReferrerMobile() {
		return referrerMobile;
	}

	public void setReferrerMobile(String referrerMobile) {
		this.referrerMobile = referrerMobile;
	}
	
}
