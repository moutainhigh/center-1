package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class UserFact {
	
	@ZapcomApi(value="商品ID",remark="8019406872")
	private String product_id = "";
	
	@ZapcomApi(value="商品名称",remark="威王弧动静音烟机灶具套组")
	private String product_name = "";
	
	@ZapcomApi(value="扫描时间",remark="2014-10-24 16:58:18")
	private String security_time = "";
	
	@ZapcomApi(value="购买时间",remark="2014-10-24 16:58:18")
	private String buy_time = "";

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getSecurity_time() {
		return security_time;
	}

	public void setSecurity_time(String security_time) {
		this.security_time = security_time;
	}

	public String getBuy_time() {
		return buy_time;
	}

	public void setBuy_time(String buy_time) {
		this.buy_time = buy_time;
	}
	
}
