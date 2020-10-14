package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiBrandProductForInput extends RootInput {
	
	@ZapcomApi(value="专题编号",require=1)
	private String infoCode="";
	
	@ZapcomApi(value="商品编号",require=1)
	private String productCodes="";

	public String getProductCodes() {
		return productCodes;
	}

	public void setProductCodes(String productCodes) {
		this.productCodes = productCodes;
	}

	public String getInfoCode() {
		return infoCode;
	}

	public void setInfoCode(String infoCode) {
		this.infoCode = infoCode;
	}
}
	