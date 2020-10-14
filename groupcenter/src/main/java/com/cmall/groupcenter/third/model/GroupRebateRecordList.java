package com.cmall.groupcenter.third.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.sun.org.apache.bcel.internal.generic.ARRAYLENGTH;

public class GroupRebateRecordList {

	@ZapcomApi(value = "订单号", remark = "订单号")
	private String orderCode="";
	
	@ZapcomApi(value = "用户编号",demo = "MI141127100121")
	String memberCode="";
	
	@ZapcomApi(value = "状态", remark = "状态")
	private String status="";
	
	@ZapcomApi(value = "时间", remark = "时间")
	private String time="";
	
	@ZapcomApi(value = "金额", remark = "金额")
	private String money="";
	
	@ZapcomApi(value = "说明", remark = "说明")
	private String description="";
	
	@ZapcomApi(value = "来源", remark = "来源")
	private String businessCode="";
	
	@ZapcomApi(value = "渠道", remark = "渠道")
	private String channel="";
	
	@ZapcomApi(value = "商品信息", remark = "商品信息")
	private List<RebateProductDetail> rebateProductList=new ArrayList<RebateProductDetail>();

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
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

	public String getBusinessCode() {
		return businessCode;
	}

	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public List<RebateProductDetail> getRebateProductList() {
		return rebateProductList;
	}

	public void setRebateProductList(List<RebateProductDetail> rebateProductList) {
		this.rebateProductList = rebateProductList;
	}
	
	
}
