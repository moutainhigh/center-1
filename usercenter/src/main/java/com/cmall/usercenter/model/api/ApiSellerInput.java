package com.cmall.usercenter.model.api;

import com.srnpr.zapcom.topapi.RootInput;

public class ApiSellerInput extends RootInput {
	
	private String sellerName = "";

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	
}
