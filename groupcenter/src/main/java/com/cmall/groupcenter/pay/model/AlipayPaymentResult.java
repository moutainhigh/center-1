package com.cmall.groupcenter.pay.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class AlipayPaymentResult {

	@ZapcomApi(value="支付宝移动支付链接",remark="")
	private String alipayUrl = "";
	@ZapcomApi(value="支付宝Sign",remark="签名过的")
	private String alipaySign = "";
	
	
	public String getAlipayUrl() {
		return alipayUrl;
	}
	public void setAlipayUrl(String alipayUrl) {
		this.alipayUrl = alipayUrl;
	}
	public String getAlipaySign() {
		return alipaySign;
	}
	public void setAlipaySign(String alipaySign) {
		this.alipaySign = alipaySign;
	}
}
