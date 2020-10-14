package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetProductsInput extends RootInput {

	
	/**
	 * 商品编号,用逗号分隔,如  a,b,c,d
	 */
	@ZapcomApi(value="商品编号")
	private String productStrs="";

	public String getProductStrs() {
		return productStrs;
	}

	public void setProductStrs(String productStrs) {
		this.productStrs = productStrs;
	}

	
	
}
	