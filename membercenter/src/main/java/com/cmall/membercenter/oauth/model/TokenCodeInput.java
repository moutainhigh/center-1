package com.cmall.membercenter.oauth.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class TokenCodeInput extends RootInput {

	@ZapcomApi(value = "回调地址", require = 1, demo = "http://www.yourdomain.com/callback?access_token=", remark = "回调的链接，会将access_token拼接到回调地址最后")
	private String backUrl = "";
	
	@ZapcomApi(value="调用类型",require=0,demo="",verify="in=site",remark="调用类型 网站：site ")
	private String callType="";

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

	public String getCallType() {
		return callType;
	}

	public void setCallType(String callType) {
		this.callType = callType;
	}
	

}
