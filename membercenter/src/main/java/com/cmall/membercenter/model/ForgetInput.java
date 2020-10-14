package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class ForgetInput extends UserInput {

	@ZapcomApi(value = "验证码", demo = "1234",  remark = "手机APP时为手机收到的短信验证码，网站注册时为图形验证码")
	private String verify_code = "";
	
	
	public String getVerify_code() {
		return verify_code;
	}

	public void setVerify_code(String verify_code) {
		this.verify_code = verify_code;
	}
	
}
