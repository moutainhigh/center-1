package com.cmall.groupcenter.wallet.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class CheckBlanceResult extends RootResultWeb{
	
	@ZapcomApi(value = "用户编号", remark = "用户编号")
	private String memberCode = "";
	
	@ZapcomApi(value = "账户余额", remark = "账户余额")
	private String blanceAccount = "";

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getBlanceAccount() {
		return blanceAccount;
	}

	public void setBlanceAccount(String blanceAccount) {
		this.blanceAccount = blanceAccount;
	}
	
	
	

}
