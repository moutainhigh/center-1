package com.cmall.groupcenter.pay.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class WechatPaymentResult {
	@ZapcomApi(value="商家在微信开放平台申请的应用id")
	private String appid = "";
	
	@ZapcomApi(value="随机串，防重发")
	private String nonceStr = "";
	
	@ZapcomApi(value="商家根据文档填写的数据和签名")
	private String packageValue = "";
	
	@ZapcomApi(value="商户id")
	private String partnerid = "";
	
	@ZapcomApi(value="预支付订单")
	private String prepayid = "";
	
	@ZapcomApi(value="商家根据微信开放平台文档对数据做的签名")
	private String sign = "";
	
	@ZapcomApi(value="当前的时间")
	private String timeStamp = "";

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getNonceStr() {
		return nonceStr;
	}

	public void setNonceStr(String nonceStr) {
		this.nonceStr = nonceStr;
	}

	public String getPackageValue() {
		return packageValue;
	}

	public void setPackageValue(String packageValue) {
		this.packageValue = packageValue;
	}

	public String getPartnerid() {
		return partnerid;
	}

	public void setPartnerid(String partnerid) {
		this.partnerid = partnerid;
	}

	public String getPrepayid() {
		return prepayid;
	}

	public void setPrepayid(String prepayid) {
		this.prepayid = prepayid;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	
}
