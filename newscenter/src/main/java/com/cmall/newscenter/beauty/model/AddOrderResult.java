package com.cmall.newscenter.beauty.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 惠美丽-提交订单输出类
 * @author houwen	
 * date 2014-10-09
 * @version 1.0
 */
public class AddOrderResult extends RootResultWeb {
	
	@ZapcomApi(value = "订单编号")
	private String order_code="";

	@ZapcomApi(value="金额")
	private String order_money = "";
	
	@ZapcomApi(value="支付方式")
	private String pay_type = "";

	public String getOrder_code() {
		return order_code;
	}

	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}

	public String getOrder_money() {
		return order_money;
	}

	public void setOrder_money(String order_money) {
		this.order_money = order_money;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}
	
}
