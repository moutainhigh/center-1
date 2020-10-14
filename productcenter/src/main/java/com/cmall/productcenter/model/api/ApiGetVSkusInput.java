package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetVSkusInput extends RootInput {
	/**
	 * sku编号,用逗号分隔,如  a,b,c,d
	 */
	private String skuStrs="";

	public String getSkuStrs() {
		return skuStrs;
	}

	public void setSkuStrs(String skuStrs) {
		this.skuStrs = skuStrs;
	}
}
