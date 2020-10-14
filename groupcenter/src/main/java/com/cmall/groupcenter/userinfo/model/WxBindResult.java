package com.cmall.groupcenter.userinfo.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;


public class WxBindResult extends RootResultWeb{

	@ZapcomApi(value = "用户认证串", remark = "登陆成功后返回非空，用于需要用户授权api_token的操作")
	private String userToken = "";
	
	@ZapcomApi(value = "用户编号")
	private String memberCode="";
	
	@ZapcomApi(value = "用户登录名")
	private String loginName="";

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	
}
