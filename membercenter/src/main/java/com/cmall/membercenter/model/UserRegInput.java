package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class UserRegInput extends UserInput {

	

	@ZapcomApi(value = "验证码", demo = "1234", require = 1, remark = "手机APP注册时为手机收到的短信验证码，网站注册时为图形验证码")
	private String verify_code = "";

	

	@ZapcomApi(value = "昵称", require = 1, remark = "昵称,最长30位", demo = "123", verify = { "maxlength=30" })
	private String nickname = "";

	

	public String getVerify_code() {
		return verify_code;
	}

	public void setVerify_code(String verify_code) {
		this.verify_code = verify_code;
	}

	

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	

}
