package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class OrderInfo { 

	@ZapcomApi(value = "订单号", remark = "订单号")
	private String orderCode="";
	
	@ZapcomApi(value = "返现时间", remark = "返现时间")
	private String reckonTime="";
	
	@ZapcomApi(value = "返利金额", remark = "返利金额")
	private String reckonMoney="";
	
	@ZapcomApi(value = "返利类型", remark = "返利类型")
	private String reckonType="";
	
	@ZapcomApi(value = "订单状态", remark = "订单状态")
	private String orderStatus="";
	
	@ZapcomApi(value = "装入可提现账户时间", remark = "转入可提现账户时间")
	private String WithdrawTime="";

	


	public String getWithdrawTime() {
		return WithdrawTime;
	}

	public void setWithdrawTime(String withdrawTime) {
		WithdrawTime = withdrawTime;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getReckonTime() {
		return reckonTime;
	}

	public void setReckonTime(String reckonTime) {
		this.reckonTime = reckonTime;
	}

	public String getReckonMoney() {
		return reckonMoney;
	}

	public void setReckonMoney(String reckonMoney) {
		this.reckonMoney = reckonMoney;
	}

	public String getReckonType() {
		return reckonType;
	}

	public void setReckonType(String reckonType) {
		this.reckonType = reckonType;
	}
}
