package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 微信签名输入类
 * @author shiyz
 * date 2016-03-21
 */
public class WeChatsSignatureInput extends RootInput {

	@ZapcomApi(value="链接地址")
	private String url = "";

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
