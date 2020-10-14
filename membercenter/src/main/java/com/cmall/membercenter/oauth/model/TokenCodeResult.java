package com.cmall.membercenter.oauth.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class TokenCodeResult extends RootResultWeb {

	@ZapcomApi(value = "临时令牌")
	private String tokenCode = "";
	@ZapcomApi(value="调用的URL",remark="获取到令牌后跳转到的url")
	private String callUrl="";

	public String getTokenCode() {
		return tokenCode;
	}

	public void setTokenCode(String tokenCode) {
		this.tokenCode = tokenCode;
	}

	public String getCallUrl() {
		return callUrl;
	}

	public void setCallUrl(String callUrl) {
		this.callUrl = callUrl;
	}

}
