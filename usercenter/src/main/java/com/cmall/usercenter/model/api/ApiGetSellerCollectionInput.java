package com.cmall.usercenter.model.api;

import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetSellerCollectionInput extends RootInput {
	
	private String sellerCodes = "";

	public String getSellerCodes() {
		return sellerCodes;
	}

	public void setSellerCodes(String sellerCodes) {
		this.sellerCodes = sellerCodes;
	}
	
}
