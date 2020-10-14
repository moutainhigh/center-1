package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetSellBrandInput extends RootInput{
	
	
	/**
	 * 当前店铺的编码，如果为空，则取当前登陆人的店铺编码
	 */
	@ZapcomApi(value="当前店铺的编码")
	private String sellerCode = "";

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	
	
}
