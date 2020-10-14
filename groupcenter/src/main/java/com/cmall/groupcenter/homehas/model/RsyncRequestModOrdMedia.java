package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestModOrdMedia implements IRsyncRequest {

	/**
	 * 订单号
	 */
	private String ord_id = "";

	public String getOrd_id() {
		return ord_id;
	}

	public void setOrd_id(String ord_id) {
		this.ord_id = ord_id;
	}
	
}
