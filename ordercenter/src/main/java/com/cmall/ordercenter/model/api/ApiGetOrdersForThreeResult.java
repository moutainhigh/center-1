package com.cmall.ordercenter.model.api;

import java.util.List;

import com.cmall.ordercenter.model.Order;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetOrdersForThreeResult extends RootResult {

	private List<Order> list = null;

	public List<Order> getList() {
		return list;
	}

	public void setList(List<Order> list) {
		this.list = list;
	}
}
