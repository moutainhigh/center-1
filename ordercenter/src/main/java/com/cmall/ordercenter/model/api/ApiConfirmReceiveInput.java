package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.topapi.RootInput;

public class ApiConfirmReceiveInput extends RootInput {
	
	private String orderCode = "";

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
}
