package com.cmall.productcenter.model;

import com.srnpr.zapcom.topapi.RootInput;

public class GetTemplateNameInput extends RootInput{
	private String templateCode="";

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}
	
}
