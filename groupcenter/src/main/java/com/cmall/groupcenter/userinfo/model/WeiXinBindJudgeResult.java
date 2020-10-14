package com.cmall.groupcenter.userinfo.model;

import com.cmall.membercenter.model.HXUserLoginInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 微信登录预判断
 * @author chenbin
 *
 */
public class WeiXinBindJudgeResult extends RootResultWeb{

	@ZapcomApi(value = "跳转页面",demo = "index.html")
	String pageUrl="";
	
	@ZapcomApi(value = "手机号",demo = "18632452312")
	String userMobile="";
	
	@ZapcomApi(value = "授权码",demo = "sdfds3232sdfdsf")
	String userToken="";
	
	@ZapcomApi(value = "用户编码",demo = "sdfds3232sdfdsf")
	String memberCode="";
	
	@ZapcomApi(value = "标识用户是否跳转绑定手机号页面",demo = "0",remark="标识用户是否跳转绑定手机号页面。0:是 1:否")
	int isPhoneBind=0;
	

	@ZapcomApi(value="获取环信登录信息")
	private HXUserLoginInfo hxUserLoginInfo = new HXUserLoginInfo();
	
	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getUserMobile() {
		return userMobile;
	}

	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public HXUserLoginInfo getHxUserLoginInfo() {
		return hxUserLoginInfo;
	}

	public void setHxUserLoginInfo(HXUserLoginInfo hxUserLoginInfo) {
		this.hxUserLoginInfo = hxUserLoginInfo;
	}
	
	public int getIsPhoneBind() {
		return isPhoneBind;
	}

	public void setIsPhoneBind(int isPhoneBind) {
		this.isPhoneBind = isPhoneBind;
	}

}
