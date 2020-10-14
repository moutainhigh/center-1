package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.topapi.RootInput;

public class ApiRetentionAdjustMoneyManageInput extends RootInput {

	
	private String smallSellerCode;
	private String adjustRetentionMoney;
	private String ajustReason;
	private String receiptRetentionMoneyCode;
	public String getReceiptRetentionMoneyCode() {
		return receiptRetentionMoneyCode;
	}
	public void setReceiptRetentionMoneyCode(String receiptRetentionMoneyCode) {
		this.receiptRetentionMoneyCode = receiptRetentionMoneyCode;
	}
	public String getSmallSellerCode() {
		return smallSellerCode;
	}
	public void setSmallSellerCode(String smallSellerCode) {
		this.smallSellerCode = smallSellerCode;
	}
	public String getAdjustRetentionMoney() {
		return adjustRetentionMoney;
	}
	public void setAdjustRetentionMoney(String adjustRetentionMoney) {
		this.adjustRetentionMoney = adjustRetentionMoney;
	}
	public String getAjustReason() {
		return ajustReason;
	}
	public void setAjustReason(String ajustReason) {
		this.ajustReason = ajustReason;
	}


}
