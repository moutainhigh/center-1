package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetMultiOrdersInput extends RootInput {
	
	
	/**
	 * 会员编号
	 */
	@ZapcomApi(value="会员编号")
	private String buyerCode = "";
	
	/**
	 * 订单编号
	 */
	@ZapcomApi(value="订单编号")
	private String orderCodes = "";

	
	
	
	
	public String getOrderCodes() {
		return orderCodes;
	}

	public void setOrderCodes(String orderCodes) {
		this.orderCodes = orderCodes;
	}
	
	
	public String getBuyerCode() {
		return buyerCode;
	}

	public void setBuyerCode(String buyerCode) {
		this.buyerCode = buyerCode;
	}
}
