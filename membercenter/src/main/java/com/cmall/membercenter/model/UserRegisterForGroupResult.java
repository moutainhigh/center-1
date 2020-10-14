package com.cmall.membercenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class UserRegisterForGroupResult extends RootResultWeb{

	@ZapcomApi(value="登录名称")
	private String loginName;
	
	@ZapcomApi(value = "流水号")
	private String serialNumber;
	
	@ZapcomApi(value = "是否可绑定上线", remark = "绑定描述", demo = "1是可绑定上线   0是不可绑定")
	private int flagRelation = 1;
	
	@ZapcomApi(value = "用户编号")
	private String memberCode;
	
	private String accountCode;
	
	@ZapcomApi(value = "用户token")
	private String userToken;
	
	@ZapcomApi(value = "是否无密码注册用户", remark = "0为不是无密码注册用户，反之1是，默认为0")
	private String isNoPassword = "0";

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public int getFlagRelation() {
		return flagRelation;
	}

	public void setFlagRelation(int flagRelation) {
		this.flagRelation = flagRelation;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
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

}
