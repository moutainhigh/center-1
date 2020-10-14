package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class GroupWopenCreateAppInput extends RootInput{

	@ZapcomApi(value = "应用名称", require = 1)
	String appName="";
	
	@ZapcomApi(value = "应用描述", require = 1)
	String appDescription="";

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppDescription() {
		return appDescription;
	}

	public void setAppDescription(String appDescription) {
		this.appDescription = appDescription;
	}
	

}
