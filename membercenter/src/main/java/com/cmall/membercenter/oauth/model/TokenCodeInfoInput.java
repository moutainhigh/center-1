package com.cmall.membercenter.oauth.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class TokenCodeInfoInput extends RootInput {

	@ZapcomApi(value = "临时令牌",require = 1)
	private String tokenCode = "";

	public String getTokenCode() {
		return tokenCode;
	}

	public void setTokenCode(String tokenCode) {
		this.tokenCode = tokenCode;
	}
	
	
}
