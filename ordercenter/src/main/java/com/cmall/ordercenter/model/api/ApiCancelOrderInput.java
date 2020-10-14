package com.cmall.ordercenter.model.api;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.topapi.RootInput;

public class ApiCancelOrderInput  extends RootInput {
	
	private String orderCodes = new String();

	public String getOrderCodes() {
		return orderCodes;
	}

	public void setOrderCodes(String orderCodes) {
		this.orderCodes = orderCodes;
	}

}
