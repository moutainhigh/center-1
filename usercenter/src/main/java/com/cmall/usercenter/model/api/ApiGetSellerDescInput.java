package com.cmall.usercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetSellerDescInput  extends RootInput {
	/**
	 * 店铺代码 
	 */
	@ZapcomApi(value="店铺代码")
	private String sellerCode = "";

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}
	
}
