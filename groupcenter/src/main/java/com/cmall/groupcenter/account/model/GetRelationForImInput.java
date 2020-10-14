package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class GetRelationForImInput extends RootInput{

	@ZapcomApi(value = "用户编号", remark="传多个以,分隔",demo = "MI15060110001,MI15060110001", require = 1)
	String memberCode="";
	
	@ZapcomApi(value = "主人用户编号", remark="主人用户编号",demo = "MI15060110001", require = 0)
	String hostMemberCode="";

	public String getHostMemberCode() {
		return hostMemberCode;
	}

	public void setHostMemberCode(String hostMemberCode) {
		this.hostMemberCode = hostMemberCode;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
}
