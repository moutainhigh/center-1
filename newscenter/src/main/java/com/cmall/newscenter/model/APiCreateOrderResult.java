package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

public class APiCreateOrderResult extends RootResult {
	
	@ZapcomApi(value="订单编号",remark="订单编号")
	private String order_code = "";
	
	@ZapcomApi(value="支付宝返回的sign信息",remark="支付宝返回的sign信息")
	private String sign_detail = "";

	@ZapcomApi(value="支付链接",remark="支付链接")
	private String pay_url = "";

	public String getOrder_code() {
		return order_code;
	}

	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}

	public String getSign_detail() {
		return sign_detail;
	}

	public void setSign_detail(String sign_detail) {
		this.sign_detail = sign_detail;
	}

	public String getPay_url() {
		return pay_url;
	}

	public void setPay_url(String pay_url) {
		this.pay_url = pay_url;
	}
	
}