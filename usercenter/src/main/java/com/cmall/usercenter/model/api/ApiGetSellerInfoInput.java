package com.cmall.usercenter.model.api;

import com.srnpr.zapcom.topapi.RootInput;



public class ApiGetSellerInfoInput extends RootInput {

	private String sellerDomain = "";

	public String getSellerDomain() {
		return sellerDomain;
	}

	public void setSellerDomain(String sellerDomain) {
		this.sellerDomain = sellerDomain;
	}
	
	
	
}
