package com.cmall.ordercenter.model.api;

import com.cmall.ordercenter.model.Order;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetOrderResult extends RootResult {
	private Order order = new Order();

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
	
	
}
