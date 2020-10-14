package com.cmall.membercenter.model;

import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webmodel.MWebResult;

public class HomePoolMLoginResult extends RootResultWeb {

	
	private String memberCode="";
	
	private String userToken="";
	
	/**
	 * 用户名
	 */
	private String memberName = "";
	
	/**
	 * 昵称
	 */
	private String nickname = "";

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

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	
}
