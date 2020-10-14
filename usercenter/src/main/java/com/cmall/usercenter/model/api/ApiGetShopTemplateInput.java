package com.cmall.usercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetShopTemplateInput extends RootInput {
	
	/**
	 * 卖家字符串
	 */
	@ZapcomApi(value="卖家字符串")
	private String sellerCodes = "";
	
	
	/**
	 * 1 只取内容 2 取头和内容
	 */
	private int type = 0;

	public String getSellerCodes() {
		return sellerCodes;
	}

	public void setSellerCodes(String sellerCodes) {
		this.sellerCodes = sellerCodes;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
	
	
}
