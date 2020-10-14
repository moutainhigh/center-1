package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class GetConsumeDetailInput extends RootInput{

	@ZapcomApi(value = "用户编号", demo = "MI15000", require = 1)
	private String memberCode = "";
	
	@ZapcomApi(value = "关联度", remark="0:自己，1：一度，2：二度",demo = "0", require = 1)
	private String relationLevel = "";

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getRelationLevel() {
		return relationLevel;
	}

	public void setRelationLevel(String relationLevel) {
		this.relationLevel = relationLevel;
	}
	
}
