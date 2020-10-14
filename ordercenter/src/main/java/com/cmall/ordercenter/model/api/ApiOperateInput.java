package com.cmall.ordercenter.model.api;

import com.cmall.ordercenter.model.OcOrderShipments;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiOperateInput extends RootInput {
	
	/**
	 * 订单编号
	 */
	@ZapcomApi(value="订单编号")
	private String orderCode = "";
	
	/**
	 * 1 取消
	 * 2 发货
	 * 3 确认收货-处理到完成
	 */
	private int type = 0;
	
	
	private OcOrderShipments oos  = new OcOrderShipments();
	

	public OcOrderShipments getOos() {
		return oos;
	}

	public void setOos(OcOrderShipments oos) {
		this.oos = oos;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
}
