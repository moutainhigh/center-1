package com.cmall.ordercenter.model;

import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 支付回调结果
 * 
 * @author srnpr
 *
 */
public class PayResult extends MWebResult {

	/**
	 * 订单编号
	 */
	private String orderCode = "";

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

}
