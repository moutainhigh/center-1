package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetSkusInput extends RootInput {
	/**
	 * sku编号,用逗号分隔,如  a,b,c,d
	 */
	@ZapcomApi(value="sku编号")
	private String skuStrs="";

	public String getSkuStrs() {
		return skuStrs;
	}

	public void setSkuStrs(String skuStrs) {
		this.skuStrs = skuStrs;
	}
	
	
}
