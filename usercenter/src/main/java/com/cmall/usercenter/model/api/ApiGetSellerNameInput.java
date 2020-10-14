package com.cmall.usercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetSellerNameInput extends RootInput {
	
	@ZapcomApi(value="系统编号",require=1)
	private String smallSellerCodes = "";

	public String getSmallSellerCodes() {
		return smallSellerCodes;
	}

	public void setSmallSellerCodes(String smallSellerCodes) {
		this.smallSellerCodes = smallSellerCodes;
	}
}
