package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetOrdersInput extends RootInput {
	
	
	/**
	 * 购买者code
	 */
	@ZapcomApi(value="购买者code")
	private String buyerCode="";
	
	
	/**
	 * 订单的从这个创建时间开始查,如果为空，则为所有
	 */
	@ZapcomApi(value="订单的从这个创建时间开始查,如果为空，则为所有")
	private String fromTime = "";
	
	
	/**
	 * 订单状态 值  说明 ,如果为空，则为所有
	 * 	4497153900010001	下单成功-未付款
	 * 	4497153900010002	下单成功-未发货
	 * 	4497153900010003	已发货
	 * 	4497153900010004	已收货
	 * 	4497153900010005	交易成功
	 * 	4497153900010006	交易失败

	 */
	@ZapcomApi(value="订单状态 值 说明 ,如果为空，则为所有")
	private String orderStatus = "";
	
	
	
	/**
	 * 订单类型	值				说明
	 * 			449715200001	商城订单
	 * 			449715200002	好物产订单
	 */
	@ZapcomApi(value="订单类型值说明")
	private String orderType="";
	
	
	
	/**
	 * 自定义订单来源
	 */
	@ZapcomApi(value="自定义订单来源")
	private String orderChannel = "";
	
	
	public String getOrderChannel() {
		return orderChannel;
	}

	public void setOrderChannel(String orderChannel) {
		this.orderChannel = orderChannel;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	public String getBuyerCode() {
		return buyerCode;
	}

	public void setBuyerCode(String buyerCode) {
		this.buyerCode = buyerCode;
	}
	
}
