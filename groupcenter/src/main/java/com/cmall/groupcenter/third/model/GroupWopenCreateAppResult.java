package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GroupWopenCreateAppResult extends RootResultWeb{

	@ZapcomApi(value = "apikey", require = 1)
	String apikey="";
	
	@ZapcomApi(value = "apipassword", require = 1)
	String apipassword="";
	
	@ZapcomApi(value = "appcode", require = 1)
	String appcode="";
	

	public String getApikey() {
		return apikey;
	}

	public void setApikey(String apikey) {
		this.apikey = apikey;
	}

	public String getApipassword() {
		return apipassword;
	}

	public void setApipassword(String apipassword) {
		this.apipassword = apipassword;
	}

	public String getAppcode() {
		return appcode;
	}

	public void setAppcode(String appcode) {
		this.appcode = appcode;
	}
	
	
	
	
}
