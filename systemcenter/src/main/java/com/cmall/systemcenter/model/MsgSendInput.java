package com.cmall.systemcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class MsgSendInput extends RootInput {
	@ZapcomApi(value = "手机号码", require = 1, remark = "手机号码", verify = "base=mobile")
	private String mobile = "";
	@ZapcomApi(value = "发送类型", require = 1, remark = "可选值：reginster(注册),login(登录),resetpassword(重置密码),forgetpassword(忘记密码),changephone(修改手机号),updateMemInfor(修改用户基本资料),binding(微公社关系绑定),weixinbind(微信绑定)。，verifyCodeLogin(验证手机登录),agentPassWord(嘉玲代理商验证)", verify = "in=reginster,login,resetpassword,forgetpassword,changephone,updateMemInfor,binding,weixinbind,verifyCodeLogin,agentPassWord")
	private String send_type = "";
	
	@ZapcomApi(value = "图片验证码",remark="图片验证码")
	private String verify_code = "";
	
	
	@ZapcomApi(value = "流水号",remark="流水号")
	private String water_code = "";
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

	public String getSend_type() {
		return send_type;
	}

	public void setSend_type(String send_type) {
		this.send_type = send_type;
	}

	public String getWater_code() {
		return water_code;
	}

	public void setWater_code(String water_code) {
		this.water_code = water_code;
	}

}
