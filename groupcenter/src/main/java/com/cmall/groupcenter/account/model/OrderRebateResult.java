package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class OrderRebateResult extends RootResultWeb {

	@ZapcomApi(value = "清分订单号", remark = "清分订单号编号")
	private String orderId = "";

	/**
	 * 获取  orderId
	 */
	public String getOrderId() {
		return orderId;
	}

	/**
	 * 设置 
	 * @param orderId 
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	

}
