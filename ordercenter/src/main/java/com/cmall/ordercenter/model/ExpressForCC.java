package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 快递信息  
 * @author zhaoxq
 *
 */
public class ExpressForCC{
	
	/**
	 * 轨迹内容
	 */
	@ZapcomApi(value="轨迹内容")
	private String context = "";
	
	/**
	 * 轨迹时间
	 */
	@ZapcomApi(value="轨迹时间")
	private String time = "";
	
	/**
	 * 订单编号
	 */
	@ZapcomApi(value="订单编号")
	private String orderCode ="";
	
	/**
	 * 运单号
	 */
	@ZapcomApi(value="运单号")
	private String waybill = "";
	
	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getWaybill() {
		return waybill;
	}

	public void setWaybill(String waybill) {
		this.waybill = waybill;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
