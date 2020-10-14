package com.cmall.groupcenter.userinfo.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class WxBindInput extends RootInput {

	@ZapcomApi(value = "用户名", remark = "用户名", demo = "13512345678", require = 0, verify = "base=mobile")
	private String loginName = "";

	@ZapcomApi(value = "用户密码", require = 0, demo = "123456", remark = "用户的密码，长度为6-40位，支持特殊字符。", verify = {
			"minlength=6", "maxlength=40" })
	private String loginPass = "";
	
	@ZapcomApi(value = "openId", require = 0, remark = "openId", demo = "234324654575")
	private String openId = "";
	
	@ZapcomApi(value = "targetId", require = 0, remark = "绑定：1，免费通知提醒：2", demo = "1")
	private String targetId = "";

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getLoginPass() {
		return loginPass;
	}

	public void setLoginPass(String loginPass) {
		this.loginPass = loginPass;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
	

}
