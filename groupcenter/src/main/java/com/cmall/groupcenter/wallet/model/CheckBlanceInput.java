package com.cmall.groupcenter.wallet.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class CheckBlanceInput extends RootInput{

	@ZapcomApi(value = "用户编号", remark = "用户编号")
	private String memberCode = "";

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	
	
	
}
