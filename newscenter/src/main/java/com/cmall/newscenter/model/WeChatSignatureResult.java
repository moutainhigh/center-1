package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 防伪码输出类
 * 
 * @author shiyz date 2014-09-20
 * 
 */
public class WeChatSignatureResult extends RootResultWeb {

	@ZapcomApi(value = "签名")
	private String signature = "";
	@ZapcomApi(value = "时间戳")
	private String timestamp = "";
	@ZapcomApi(value = "随机串")
	private String nonceStr = "";
	@ZapcomApi(value = "调用API")
	private String[] jsApiList = null;
	@ZapcomApi(value = "appid")
	private String appId = "";

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String[] getJsApiList() {
		return jsApiList;
	}

	public void setJsApiList(String[] jsApiList) {
		this.jsApiList = jsApiList;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

}
