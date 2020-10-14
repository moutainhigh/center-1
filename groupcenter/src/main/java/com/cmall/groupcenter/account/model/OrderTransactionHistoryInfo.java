package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 返利详情中的订单交易节点信息
 * @author GaoYang
 *
 */
public class OrderTransactionHistoryInfo {
	
	@ZapcomApi(value = "订单编号", remark = "订单编号")
	private String orderCode="";
	
	@ZapcomApi(value = "交易时间", remark = "交易时间")
	private String transactionTime="";
	
	@ZapcomApi(value = "交易状态", remark = "交易状态")
	private String transactionStatus="";

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getTransactionTime() {
		return transactionTime;
	}

	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public void setTransactionStatus(String transactionStatus) {
		this.transactionStatus = transactionStatus;
	}
	
	
	
}
