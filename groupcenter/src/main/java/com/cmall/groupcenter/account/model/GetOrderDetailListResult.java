package com.cmall.groupcenter.account.model;

import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetOrderDetailListResult extends RootResultWeb{

	@ZapcomApi(value = "订单信息",remark="订单信息")
	List<OrderInfo> orderList=null;

	public List<OrderInfo> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<OrderInfo> orderList) {
		this.orderList = orderList;
	}
}
