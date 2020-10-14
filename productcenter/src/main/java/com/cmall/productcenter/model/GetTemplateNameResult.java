package com.cmall.productcenter.model;

import com.srnpr.zapcom.topapi.RootResult;

public class GetTemplateNameResult extends RootResult{
	private String templateName="";

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
}
