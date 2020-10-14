package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class UserInput extends RootInput {

	@ZapcomApi(value = "用户名", remark = "用户名，client_source为app时该字段为手机号码。", demo = "13512345678", require = 1, verify = "regex=^[a-zA-Z0-9_\\-\\@\\.]{4,40}$")
	private String login_name = "";

	@ZapcomApi(value = "用户密码", require = 1, demo = "123456", remark = "用户的密码，长度为6-40位，支持特殊字符。", verify = {
			"minlength=4", "maxlength=40" })
	private String password = "";

	

	@ZapcomApi(value = "客户端来源", require = 1, remark = "注册类型。可选值:app(手机APP注册)，site(网站注册)。手机APP注册时会发送手机验证码。", demo = "app", verify = "in=app,site")
	private String client_source = "";

	public String getLogin_name() {
		return login_name;
	}

	public void setLogin_name(String login_name) {
		this.login_name = login_name;
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
