package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class AccountInput extends RootInput {
	
	/**
	 *店铺编号 
	 */
	@ZapcomApi(value="店铺编号")
	private String seller_code = "";

	public String getSeller_code() {
		return seller_code;
	}

	public void setSeller_code(String seller_code) {
		this.seller_code = seller_code;
	}
	
}
