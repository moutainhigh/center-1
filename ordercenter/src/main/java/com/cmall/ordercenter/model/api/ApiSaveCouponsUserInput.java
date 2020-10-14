package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiSaveCouponsUserInput  extends RootInput {
	
	@ZapcomApi(value = "手机号", require = 1, remark = "手机号", demo = "13388888888", verify = "base=mobile")
	private String mobile="";
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
