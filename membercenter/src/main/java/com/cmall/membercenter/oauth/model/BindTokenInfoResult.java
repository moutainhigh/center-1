package com.cmall.membercenter.oauth.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class BindTokenInfoResult extends RootResultWeb {

	@ZapcomApi(value = "回调地址", require = 1, demo = "http://www.yourdomain.com/callback?access_token=", remark = "回调的链接，会将access_token拼接到回调地址最后")
	private String backUrl = "";

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}

}
