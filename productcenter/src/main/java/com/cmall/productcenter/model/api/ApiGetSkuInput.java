package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetSkuInput extends RootInput {
	/**
	 * sku编号
	 */
	@ZapcomApi(value="sku编号",require=1)
	private String skuCode="";

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	
}
