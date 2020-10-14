package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class UserRegHomePoolInput extends UserInput {

	@ZapcomApi(value = "验证码", demo = "1234", require = 1, remark = "手机APP注册时为手机收到的短信验证码，网站注册时为图形验证码")
	private String verify_code = "";

	@ZapcomApi(value = "手机号", require = 1, remark = "手机号", demo = "13333333333", verify = { "base=mobile" })
	private String mobile = "";
	
	@ZapcomApi(value = "真实姓名", require = 0, remark = "真实姓名")
	private String realName = "";
	
	@ZapcomApi(value = "家有LD系统用户code", require = 0, remark = "家有LD系统用户code")
	private String cusId = "";

	

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

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getCusId() {
		return cusId;
	}

	public void setCusId(String cusId) {
		this.cusId = cusId;
	}
	

}
