package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GroupPayResult extends RootResultWeb{

	@ZapcomApi(value = "支付流水号",demo = "VPAY141127100121")
	String tradeCode="";
	
	@ZapcomApi(value = "订单号",demo = "141127100121")
	String orderCode="";
	
	@ZapcomApi(value = "交易金额",demo = "22.33")
	String tradeMoney="";

	public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getTradeMoney() {
		return tradeMoney;
	}

	public void setTradeMoney(String tradeMoney) {
		this.tradeMoney = tradeMoney;
	}
	
}
