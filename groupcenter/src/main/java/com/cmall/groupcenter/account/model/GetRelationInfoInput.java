package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class GetRelationInfoInput extends RootInput {

	@ZapcomApi(value = "用户手机号", remark = "用户手机号", demo = "18663936666", require = 1)
	private String mobile = "";

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
