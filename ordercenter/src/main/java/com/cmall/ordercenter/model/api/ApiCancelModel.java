package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.topapi.RootResult;

public class ApiCancelModel extends RootResult {
	
	/**
	 * 订单编号
	 */
	private String orderCode = "";
	
	

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

}
