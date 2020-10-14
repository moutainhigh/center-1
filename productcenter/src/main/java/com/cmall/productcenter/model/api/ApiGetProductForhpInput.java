package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetProductForhpInput extends RootInput {
	/**
	 * 商品编号
	 */
	@ZapcomApi(value="商品编号",require=1)
	private String productCode="";

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
}
