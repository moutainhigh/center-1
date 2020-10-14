package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 判断验证码是否正确——输入类
 * @author yangrong
 * date: 2014-09-19
 * @version1.0
 */
public class VerifyCodeIsTrueInput extends RootInput {
	
	@ZapcomApi(value = "手机号",demo= "13111102501",require = 1)
	private String phone  = "";

	@ZapcomApi(value = "验证码",demo= "获取验证码接口获取",require = 1)
	private String verify  = "";
	
	@ZapcomApi(value = "发送类型",require = 1,remark = "可选值：reginster(注册),forgetpassword(忘记密码)。", verify = "in=reginster,forgetpassword")
	private String type= "";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getVerify() {
		return verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}
	
	
	
}
