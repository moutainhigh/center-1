package com.cmall.groupcenter.report.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ReportBlackInput extends RootInput {
	
	@ZapcomApi(value="用户编号",remark="用户编号",require=1)
	private String member_code = "";

	public String getMember_code() {
		return member_code;
	}

	public void setMember_code(String member_code) {
		this.member_code = member_code;
	}
	
}