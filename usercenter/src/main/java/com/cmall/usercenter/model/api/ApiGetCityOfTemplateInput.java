package com.cmall.usercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetCityOfTemplateInput extends RootInput {
	@ZapcomApi(value="模板编号",remark="模板编号")
	private String templateCode = "";

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
}
