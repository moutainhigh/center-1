package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class UserLogoutInput extends RootInput {

	
	@ZapcomApi(value = "流水号", require = 0, remark = "流水号app传递过来", demo = "234324654575")
	private String serialNumber = "";

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	
}
