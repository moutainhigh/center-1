package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiWechatProcessInput extends RootInput{
	@ZapcomApi(value="浏览器IP地址",remark="192.168.1.142",require=1)
	private String ip="";
	@ZapcomApi(value="订单编号",require=1)
	private String orderCode="";
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	
	
}
