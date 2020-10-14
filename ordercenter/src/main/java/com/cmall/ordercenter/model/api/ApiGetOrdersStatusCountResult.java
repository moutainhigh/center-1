package com.cmall.ordercenter.model.api;

import java.util.List;

import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderStatusGroupModel;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetOrdersStatusCountResult extends RootResult {

	private OrderStatusGroupModel osgm = null;

	public OrderStatusGroupModel getOsgm() {
		return osgm;
	}

	public void setOsgm(OrderStatusGroupModel osgm) {
		this.osgm = osgm;
	}

	
}
