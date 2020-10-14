package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class GroupReconciliationDetail {

	@ZapcomApi(value = "账单类型",remark="4497465200200001-支付，4497465200200002-退款")
	String type="";
	
	@ZapcomApi(value = "流水号",remark="支付单为支付流水号，退款单为退款流水号")
	String tradeCode="";
	
	@ZapcomApi(value = "订单号",remark="相应订单号")
	String orderCode="";
	
	@ZapcomApi(value = "金额",remark="相应金额")
	String tradeMoney="";
	
	@ZapcomApi(value = "状态",remark="4497465200190001-支付成功，4497465200190002-退款成功")
	String tradeStatus="";
	
	@ZapcomApi(value = "时间",remark="相应时间")
	String time="";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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

	public String getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	
}
