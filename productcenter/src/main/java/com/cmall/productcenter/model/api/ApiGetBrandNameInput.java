package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetBrandNameInput extends RootInput {

	/**
	 * @author zb 
	 */
	@ZapcomApi(value="品牌编号")
	private String brandCode="";

	public String getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}


	
	
}
	