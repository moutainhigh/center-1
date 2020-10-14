package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.topapi.RootInput;

public class ApiRetentionMoneyManageInput extends RootInput {

	private String smallSellerCode;
	private String receiveRetentionMoney;
	private String receiveRetentionMoneyDate;
	private String adjustRetentionMoney;
	private String adjustRetentionMoneyDate;
	private Integer type;
	private String remark;

	public String getSmallSellerCode() {
		return smallSellerCode;
	}

	public void setSmallSellerCode(String smallSellerCode) {
		this.smallSellerCode = smallSellerCode;
	}

	public String getReceiveRetentionMoney() {
		return receiveRetentionMoney;
	}

	public void setReceiveRetentionMoney(String receiveRetentionMoney) {
		this.receiveRetentionMoney = receiveRetentionMoney;
	}

	public String getReceiveRetentionMoneyDate() {
		return receiveRetentionMoneyDate;
	}

	public void setReceiveRetentionMoneyDate(String receiveRetentionMoneyDate) {
		this.receiveRetentionMoneyDate = receiveRetentionMoneyDate;
	}

	public String getAdjustRetentionMoney() {
		return adjustRetentionMoney;
	}

	public void setAdjustRetentionMoney(String adjustRetentionMoney) {
		this.adjustRetentionMoney = adjustRetentionMoney;
	}

	public String getAdjustRetentionMoneyDate() {
		return adjustRetentionMoneyDate;
	}

	public void setAdjustRetentionMoneyDate(String adjustRetentionMoneyDate) {
		this.adjustRetentionMoneyDate = adjustRetentionMoneyDate;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
