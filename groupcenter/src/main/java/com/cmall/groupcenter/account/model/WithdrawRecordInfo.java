package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 记录状态
 * @author chenbin
 *
 */
public class WithdrawRecordInfo {

	@ZapcomApi(value = "状态", remark = "状态")
	private String status="";
	
	@ZapcomApi(value = "时间", remark = "时间")
	private String time="";
	
	@ZapcomApi(value = "金额", remark = "金额")
	private String money="";
	
	@ZapcomApi(value = "说明", remark = "说明")
	private String description="";
	
	@ZapcomApi(value = "pc端时间", remark = "pc端需要返回时间,不需要判断是否是今天或昨天")
	private String pcTime="";

	@ZapcomApi(value = "订单号", remark = "订单号")
	private String orderCode="";

	@ZapcomApi(value = "来源", remark = "来源")
	private String manageCode="";
	
	public String getPcTime() {
		return pcTime;
	}

	public void setPcTime(String pcTime) {
		this.pcTime = pcTime;
	}

	
	
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}


    public String getManageCode() {
        return manageCode;
    }

    public void setManageCode(String manageCode) {
        this.manageCode = manageCode;
    }
}
