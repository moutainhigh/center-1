package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestOrderStatus implements IRsyncRequest {

	/**
	 * 订单编号
	 */
	private String yc_orderform_num = "";

	public String getYc_orderform_num() {
		return yc_orderform_num;
	}

	public void setYc_orderform_num(String yc_orderform_num) {
		this.yc_orderform_num = yc_orderform_num;
	}

	
}
