package com.cmall.groupcenter.userinfo.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class WeiXinBindInput extends RootInput{

	@ZapcomApi(value = "用户名", remark = "用户名", demo = "13512345678", require = 1, verify = "base=mobile")
	private String loginName = "";

	@ZapcomApi(value = "验证码", demo = "123456", require = 1, remark = "手机APP注册时为手机收到的短信验证码")
	private String verifyCode = "";
	
	@ZapcomApi(value = "流水号", require = 0, remark = "流水号app传递过来", demo = "234324654575")
	private String serialNumber = "";

	@ZapcomApi(value = "openid",demo = "dsfsdfdfdfsf3232", require = 1)
	String openId="";

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
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

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

}
