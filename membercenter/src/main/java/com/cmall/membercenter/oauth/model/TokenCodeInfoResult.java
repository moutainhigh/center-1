package com.cmall.membercenter.oauth.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class TokenCodeInfoResult extends RootResultWeb {

	@ZapcomApi(value="返回的url",remark="根据临时令牌获取返回url")
	private String backUrl="";

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}
	
	
}
