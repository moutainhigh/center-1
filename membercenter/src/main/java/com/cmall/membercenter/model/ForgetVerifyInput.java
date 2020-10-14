package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ForgetVerifyInput extends RootInput {
	
	@ZapcomApi(value = "用户名", remark = "用户名，client_source为app时该字段为手机号码。", demo = "13512345678", require = 1, verify = "regex=^[a-zA-Z0-9_\\-\\@\\.]{4,40}$")
	private String login_name = "";
	

	@ZapcomApi(value = "验证码", demo = "1234", require = 1, remark = "手机APP时为手机收到的短信验证码，网站注册时为图形验证码")
	private String verify_code = "";
	
	
	@ZapcomApi(value = "客户端来源", require = 1, remark = "注册类型。可选值:app(手机APP注册)，site(网站注册)。手机APP注册时会发送手机验证码。", demo = "app", verify = "in=app,site")
	private String client_source = "";
	
	@ZapcomApi(value = "发送类型", require = 0, remark = "可选值：reginster(注册),login(登录),resetpassword(重置密码),forgetpassword(忘记密码),changephone(修改手机号),updateMemInfor(修改用户基本资料),binding(微公社关系绑定),weixinbind(微信绑定)。,verifyCodeLogin(短信登陆),agentPassWord(代理商)", verify = "in=reginster,login,resetpassword,forgetpassword,changephone,updateMemInfor,binding,weixinbind,verifyCodeLogin,agentPassWord")
	private String send_type = "";
	
	public String getVerify_code() {
		return verify_code;
	}

	public void setVerify_code(String verify_code) {
		this.verify_code = verify_code;
	}

	public String getLogin_name() {
		return login_name;
	}

	public void setLogin_name(String login_name) {
		this.login_name = login_name;
	}

	public String getClient_source() {
		return client_source;
	}

	public void setClient_source(String client_source) {
		this.client_source = client_source;
	}

	public String getSend_type() {
		return send_type;
	}

	public void setSend_type(String send_type) {
		this.send_type = send_type;
	}
	
}
