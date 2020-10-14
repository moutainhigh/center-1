package com.cmall.groupcenter.pay.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiPayInfoInput extends RootInput{
	@ZapcomApi(value="订单编号",demo="DD140219100010",require=1)
	private String[] orderCodes = new String[]{};
	@ZapcomApi(value="支付类型",remark="449746280003: 支付宝支付,  449746280005: 微信支付",require=1)
	private String type = "";
	@ZapcomApi(value="ip地址",require=1)
	private String ip = "";
	
	public String[] getOrderCodes() {
		return orderCodes;
	}
	public void setOrderCodes(String[] orderCodes) {
		this.orderCodes = orderCodes;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
}
