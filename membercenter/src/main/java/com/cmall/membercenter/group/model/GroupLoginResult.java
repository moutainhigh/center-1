package com.cmall.membercenter.group.model;

import com.cmall.membercenter.model.HXUserLoginInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GroupLoginResult extends RootResultWeb {

	@ZapcomApi(value = "是否无密码注册用户", remark = "0为不是无密码注册用户，反之1是，默认为0")
	private String isNoPassword = "0";
	
	@ZapcomApi(value = "用户认证串", remark = "登陆成功后返回非空，用于需要用户授权api_token的操作")
	private String userToken = "";
	
	@ZapcomApi(value = "用户编号")
	private String memberCode="";
	
	@ZapcomApi(value="获取环信登录信息")
	private HXUserLoginInfo hxUserLoginInfo = new HXUserLoginInfo();
	
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

	public String getIsNoPassword() {
		return isNoPassword;
	}

	public void setIsNoPassword(String isNoPassword) {
		this.isNoPassword = isNoPassword;
	}

	public HXUserLoginInfo getHxUserLoginInfo() {
		return hxUserLoginInfo;
	}

	public void setHxUserLoginInfo(HXUserLoginInfo hxUserLoginInfo) {
		this.hxUserLoginInfo = hxUserLoginInfo;
	}

}
