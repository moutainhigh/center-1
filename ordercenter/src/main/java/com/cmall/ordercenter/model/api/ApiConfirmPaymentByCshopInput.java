package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.topapi.RootInput;

public class ApiConfirmPaymentByCshopInput extends RootInput {

	/**
	 * 结算单编号
	 */
	private String settleCode;

	/**
	 * 商户编码
	 */
	private String merchantCode;

	private String settleType;

	public String getSettleType() {
		return settleType;
	}

	public void setSettleType(String settleType) {
		this.settleType = settleType;
	}

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public String getSettleCode() {
		return settleCode;
	}

	public void setSettleCode(String settleCode) {
		this.settleCode = settleCode;
	}

}
