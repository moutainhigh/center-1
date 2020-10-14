package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetSellerActivitysInput extends RootInput {
	
	/**
	 * 卖家code
	 */
	@ZapcomApi(value="卖家code")
	private String sellerCode="";

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}
	
	
	
}
