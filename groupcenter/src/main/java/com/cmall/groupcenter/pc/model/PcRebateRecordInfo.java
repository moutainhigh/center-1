package com.cmall.groupcenter.pc.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * PC版本返利内容
 * @author GaoYang
 *
 */
public class PcRebateRecordInfo {
	
	@ZapcomApi(value = "订单时间", remark = "订单时间")
	private String orderCreateTime="";
	
	@ZapcomApi(value = "可返利金额", remark = "可返利金额")
	private String rebateMoney="";
	
	@ZapcomApi(value = "订单状态", remark = "订单状态")
	private String orderStatus="";
	
	@ZapcomApi(value = "返利状态", remark = "返利状态")
	private String rebateStatus="";
	
	@ZapcomApi(value = "返利说明", remark = "返利说明")
	private String rebateDescription="";

	public String getOrderCreateTime() {
		return orderCreateTime;
	}

	public void setOrderCreateTime(String orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}

	public String getRebateMoney() {
		return rebateMoney;
	}

	public void setRebateMoney(String rebateMoney) {
		this.rebateMoney = rebateMoney;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getRebateStatus() {
		return rebateStatus;
	}

	public void setRebateStatus(String rebateStatus) {
		this.rebateStatus = rebateStatus;
	}

	public String getRebateDescription() {
		return rebateDescription;
	}

	public void setRebateDescription(String rebateDescription) {
		this.rebateDescription = rebateDescription;
	}

}
