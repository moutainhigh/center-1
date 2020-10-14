package com.cmall.groupcenter.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderDetail;
import com.cmall.dborm.txmodel.groupcenter.GcReckonOrderInfo;

public class ReckonOrderInfo {

	/**
	 * 商品信息
	 */
	private GcReckonOrderInfo orderInfo = new GcReckonOrderInfo();

	/**
	 * 商品明细
	 */
	private List<GcReckonOrderDetail> orderList = new ArrayList<GcReckonOrderDetail>();

	public GcReckonOrderInfo getOrderInfo() {
		return orderInfo;
	}

	public void setOrderInfo(GcReckonOrderInfo orderInfo) {
		this.orderInfo = orderInfo;
	}

	public List<GcReckonOrderDetail> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<GcReckonOrderDetail> orderList) {
		this.orderList = orderList;
	}

}
