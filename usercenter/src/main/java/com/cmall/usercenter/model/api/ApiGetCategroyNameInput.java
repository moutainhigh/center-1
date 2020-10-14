package com.cmall.usercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetCategroyNameInput extends RootInput {
	@ZapcomApi(value="分类编号",remark="多个之间用英文逗号隔开")
	private String categoryCodes = "";
	
	@ZapcomApi(value="分类所属系统编号")
	private String sellerCode = "";

	public String getCategoryCodes() {
		return categoryCodes;
	}

	public void setCategoryCodes(String categoryCodes) {
		this.categoryCodes = categoryCodes;
	}

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}
	
}
