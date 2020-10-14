package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class GroupAccountInfoInput extends RootInput{

	@ZapcomApi(value = "用户编号",demo = "MI141127100121", require = 1)
	String memberCode="";

	@ZapcomApi(value = "清分订单号",demo = "20914937")
	String reckonOrderCode="";

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getReckonOrderCode() {
		return reckonOrderCode;
	}

	public void setReckonOrderCode(String reckonOrderCode) {
		this.reckonOrderCode = reckonOrderCode;
	}
}
