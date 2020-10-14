package com.cmall.ordercenter.model;
/**
 * 支付宝接口传入参数
 * @author Administrator
 *
 */
public class AlipayProcessInput {
	private String subject = "";//商品名称
	private String payment_type = "";//支付类型
	private String total_fee = "";//交易金额
	private String seller_id = "";//卖家支付宝用户
	
	private String partner = "";//合作者身份ID
	private String input_charset = "";//参数编码字符集
	private String service =  "";//接口名称
	
	private String notify_url = "";//服务器异步通知页面路径
	private String out_trade_no = "";//商户网站唯一订单号
	private String return_url = ""; //页面跳转同步通知页面路径
	
	private String seller_email = ""; //支付宝
	private String key = ""; 
	private String sign_type = "";
	
	private String defaultbank = ""; //默认网关
	private String paymethod = ""; // 默认支付方式
	
	
	
	public String getPaymethod() {
		return paymethod;
	}
	public void setPaymethod(String paymethod) {
		this.paymethod = paymethod;
	}
	public String getDefaultbank() {
		return defaultbank;
	}
	public void setDefaultbank(String defaultbank) {
		this.defaultbank = defaultbank;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getSign_type() {
		return sign_type;
	}
	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}
	public String getSeller_email() {
		return seller_email;
	}
	public void setSeller_email(String seller_email) {
		this.seller_email = seller_email;
	}
	public String getReturn_url() {
		return return_url;
	}
	public void setReturn_url(String return_url) {
		this.return_url = return_url;
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
	public String getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}
	public String getSeller_id() {
		return seller_id;
	}
	public void setSeller_id(String seller_id) {
		this.seller_id = seller_id;
	}
	public String getPartner() {
		return partner;
	}
	public void setPartner(String partner) {
		this.partner = partner;
	}
	public String getInput_charset() {
		return input_charset;
	}
	public void setInput_charset(String input_charset) {
		this.input_charset = input_charset;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getNotify_url() {
		return notify_url;
	}
	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}
	
	
	
}
