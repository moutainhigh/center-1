package com.cmall.membercenter.group.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class HomePoolLoginResult extends RootResultWeb {

	@ZapcomApi(value = "用户认证串", remark = "登陆成功后返回非空，用于需要用户授权api_token的操作")
	private String userToken = "";
	
	/**
	 * 用户名
	 */
	private String memberName = "";
	
	/**
	 * 昵称
	 */
	private String nickname = "";
	/**
	 * 手机号
	 */
	private String mobile = "";
	/**
	 * 邮箱
	 */
	private String email = "";
	
	/**
	 * 用户code
	 */
	private String memberCode = "";
	
	/**
	 * 家有老用户code
	 */
	private String oldCode = "";

	
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

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getOldCode() {
		return oldCode;
	}

	public void setOldCode(String oldCode) {
		this.oldCode = oldCode;
	}

}
