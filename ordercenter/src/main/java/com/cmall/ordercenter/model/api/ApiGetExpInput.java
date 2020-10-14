package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetExpInput extends RootInput {
	

	/**
	 * 订单编号
	 */
	@ZapcomApi(value="订单编号")
	private String orderCode = "";

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
}
