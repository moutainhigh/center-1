package com.cmall.systemcenter.model;

import com.srnpr.zapcom.topapi.RootInput;

public class UserLoginVerifyInput extends RootInput {

	/**
	 * 登录名
	 */
	private String loginName = "";

	/**
	 * 登录密码
	 */
	private String loginPass = "";
	/**
	 * 登录短信验证码
	 */
	private String mobileVerify = "";

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

	public String getMobileVerify() {
		return mobileVerify;
	}

	public void setMobileVerify(String mobileVerify) {
		this.mobileVerify = mobileVerify;
	}

}
