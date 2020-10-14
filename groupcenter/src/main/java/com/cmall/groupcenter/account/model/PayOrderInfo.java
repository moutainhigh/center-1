package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class PayOrderInfo {

	@ZapcomApi(value = "提现日期", remark = "提现日期")
	private String withdrawTime;
	
	@ZapcomApi(value = "提现账户", remark = "提现账户")
	private String withdrawAccount;
	
	@ZapcomApi(value = "提现金额", remark = "提现金额")
	private String withdrawMoney;
	
	@ZapcomApi(value = "账户剩余金额", remark = "账户剩余金额")
	private String afterWithdrawMoney;
	
	@ZapcomApi(value = "提款状态", remark = "提款状态")
	private String orderStatus;
	
	@ZapcomApi(value = "支付状态", remark = "支付状态")
	private String payStatus;

	public String getWithdrawTime() {
		return withdrawTime;
	}

	public void setWithdrawTime(String withdrawTime) {
		this.withdrawTime = withdrawTime;
	}

	public String getWithdrawAccount() {
		return withdrawAccount;
	}

	public void setWithdrawAccount(String withdrawAccount) {
		this.withdrawAccount = withdrawAccount;
	}

	public String getWithdrawMoney() {
		return withdrawMoney;
	}

	public void setWithdrawMoney(String withdrawMoney) {
		this.withdrawMoney = withdrawMoney;
	}

	public String getAfterWithdrawMoney() {
		return afterWithdrawMoney;
	}

	public void setAfterWithdrawMoney(String afterWithdrawMoney) {
		this.afterWithdrawMoney = afterWithdrawMoney;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getPayStatus() {
		return payStatus;
	}

	public void setPayStatus(String payStatus) {
		this.payStatus = payStatus;
	}

}
