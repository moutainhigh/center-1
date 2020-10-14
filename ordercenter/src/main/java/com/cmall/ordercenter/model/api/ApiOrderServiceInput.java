package com.cmall.ordercenter.model.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderBase;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiOrderServiceInput extends RootInput {
	
	private List<Order> orderList = new ArrayList<Order>();

	public List<Order> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<Order> orderList) {
		this.orderList = orderList;
	}
}
