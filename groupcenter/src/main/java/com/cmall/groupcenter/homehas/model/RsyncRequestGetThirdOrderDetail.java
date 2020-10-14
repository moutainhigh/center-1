package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 5.2.6 LD订单详情
 * @author cc
 *
 */
public class RsyncRequestGetThirdOrderDetail implements IRsyncRequest {
	
	/**
	 * 订单编号
	 */
	private String ord_id = "";
	
	/**
	 * 订单序号（大订单号传空，小订单号传具体的序号）
	 */
	private String ord_seq = "";

	public String getOrd_id() {
		return ord_id;
	}

	public void setOrd_id(String ord_id) {
		this.ord_id = ord_id;
	}

	public String getOrd_seq() {
		return ord_seq;
	}

	public void setOrd_seq(String ord_seq) {
		this.ord_seq = ord_seq;
	}
	
	
}
