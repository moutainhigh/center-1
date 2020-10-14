package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetSkuPriceAndStockInput extends RootInput {
	
	
	/**
	 * 会员编号
	 */
	@ZapcomApi(value="会员编号")
	private String skuStrs = "";

	public String getSkuStrs() {
		return skuStrs;
	}

	public void setSkuStrs(String skuStrs) {
		this.skuStrs = skuStrs;
	}
	
}
