package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class TVUserRegHomePoolInput extends RootInput {

	

	@ZapcomApi(value = "验证码", demo = "1234", require = 1, remark = "手机APP注册时为手机收到的短信验证码，网站注册时为图形验证码")
	private String verify_code = "";

	@ZapcomApi(value = "手机号", require = 1, remark = "手机号", demo = "13333333333", verify = { "base=mobile" })
	private String mobile = "";

	@ZapcomApi(value = "用户名", remark = "用户名，client_source为app时该字段为手机号码。", demo = "13512345678", require = 1, verify = "regex=^[a-zA-Z0-9_\\-\\@\\.]{4,40}$")
	private String login_name = "";
	
	@ZapcomApi(value = "真实姓名", remark = "真实姓名", require = 1)
	private String realName = "";

	@ZapcomApi(value = "用户密码", require = 1, demo = "123456", remark = "用户的密码，长度为6-40位，支持特殊字符。", verify = {
			"minlength=6", "maxlength=40" })
	private String password = "";

	@ZapcomApi(value = "客户端来源", require = 1, remark = "注册类型。可选值:app(手机APP注册)，site(网站注册)。手机APP注册时会发送手机验证码。", demo = "app", verify = "in=app,site")
	private String client_source = "";

	public String getVerify_code() {
		return verify_code;
	}

	public void setVerify_code(String verify_code) {
		this.verify_code = verify_code;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getLogin_name() {
		return login_name;
	}

	public void setLogin_name(String login_name) {
		this.login_name = login_name;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getClient_source() {
		return client_source;
	}

	public void setClient_source(String client_source) {
		this.client_source = client_source;
	}

	
	

}
