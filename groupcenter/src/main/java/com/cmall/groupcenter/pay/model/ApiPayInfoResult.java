package com.cmall.groupcenter.pay.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiPayInfoResult extends RootResultWeb{
	@ZapcomApi(value="支付宝支付返回参数")
	private AlipayPaymentResult  alipayPayment = new AlipayPaymentResult();
	
	@ZapcomApi(value="微信支付返回参数")
	private WechatPaymentResult wechatResult = new WechatPaymentResult();

	public AlipayPaymentResult getAlipayPayment() {
		return alipayPayment;
	}

	public void setAlipayPayment(AlipayPaymentResult alipayPayment) {
		this.alipayPayment = alipayPayment;
	}

	public WechatPaymentResult getWechatResult() {
		return wechatResult;
	}

	public void setWechatResult(WechatPaymentResult wechatResult) {
		this.wechatResult = wechatResult;
	}
	
	
	
}
