package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiAlipayMoveProcessOrderResult extends RootResult{
	@ZapcomApi(value="接口名称")
	private String service;
	@ZapcomApi(value="合作者身份ID")
	private String partner;
	@ZapcomApi(value="参数编码字符集")
	private String _input_charset;
	@ZapcomApi(value="签名方式")
	private String sign_type;
	@ZapcomApi(value="签名")
	private String sign;
	@ZapcomApi(value="服务器异步通知页面路径")
	private String notify_url;
	@ZapcomApi(value="商户网站唯一订单号")
	private String out_trade_no;
	@ZapcomApi(value="商品名称")
	private String subject;
	@ZapcomApi(value="支付类型")
	private String payment_type;
	@ZapcomApi(value="卖家支付宝账号")
	private String seller_id;
	@ZapcomApi(value="总金额")
	private String total_fee;
	@ZapcomApi(value="商品详情")
	private String body;
	
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getPartner() {
		return partner;
	}
	public void setPartner(String partner) {
		this.partner = partner;
	}
	public String get_input_charset() {
		return _input_charset;
	}
	public void set_input_charset(String _input_charset) {
		this._input_charset = _input_charset;
	}
	public String getSign_type() {
		return sign_type;
	}
	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getPayment_type() {
		return payment_type;
	}
	public void setPayment_type(String payment_type) {
		this.payment_type = payment_type;
	}
	public String getSeller_id() {
		return seller_id;
	}
	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}
	public String getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
}
