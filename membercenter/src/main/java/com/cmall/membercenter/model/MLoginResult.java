package com.cmall.membercenter.model;

import com.srnpr.zapweb.webapi.RootResultWeb;

public class MLoginResult extends RootResultWeb {

	private String isNoPassword = "0";
	
	private boolean isFirstLogin = false;
	
	private String memberCode="";
	
	private String userToken="";

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getIsNoPassword() {
		return isNoPassword;
	}

	public void setIsNoPassword(String isNoPassword) {
		this.isNoPassword = isNoPassword;
	}

	public boolean isFirstLogin() {
		return isFirstLogin;
	}

	public void setFirstLogin(boolean isFirstLogin) {
		this.isFirstLogin = isFirstLogin;
	}
	
	
}
