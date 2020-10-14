package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class GroupPayInput extends RootInput{

	@ZapcomApi(value = "用户编号",demo = "MI141127100121", require = 1)
	String memberCode="";
	
	@ZapcomApi(value = "金额",demo = "123.32", require = 1,verify="base=money")
	String tradeMoney="";
	
	@ZapcomApi(value = "订单号",demo = "12323423", require = 1)
	String orderCode="";
	
	@ZapcomApi(value = "订单创建时间",demo = "2015-04-15 14:34:23", require = 1,verify="base=datetime")
	String orderCreateTime="";
	
	@ZapcomApi(value = "名称",demo = "商品名称", require = 0)
	String tradeName="";
	
	@ZapcomApi(value = "备注",demo = "备注", require = 0)
	String remark="";

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getTradeMoney() {
		return tradeMoney;
	}

	public void setTradeMoney(String tradeMoney) {
		this.tradeMoney = tradeMoney;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getOrderCreateTime() {
		return orderCreateTime;
	}

	public void setOrderCreateTime(String orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}

	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
