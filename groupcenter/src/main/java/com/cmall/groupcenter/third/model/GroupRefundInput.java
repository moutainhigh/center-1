package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class GroupRefundInput extends RootInput{

	@ZapcomApi(value = "支付流水号",demo = "VPAY141127100121", require = 1)
	String tradeCode="";
	
	@ZapcomApi(value = "用户编号",demo = "MI141127100121", require = 1)
	String memberCode=""; 
	
	@ZapcomApi(value = "退款金额",demo = "23.11", require = 1,verify="base=money")
	String refundMoney="";
	
	@ZapcomApi(value = "订单编号",demo = "23423423", require = 1)
	String orderCode="";
	
	@ZapcomApi(value = "退款时间",demo = "2015-04-17 13:23:34", require = 1,verify="base=datetime")
	String refundTime="";
	
	@ZapcomApi(value = "备注",demo = "备注", require = 0)
	String remark="";
	
	@ZapcomApi(value = "退款编号",demo = "REF2342342", require =0)
	String businessTradeCode="";

	public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	public String getMemberCode() {
		return memberCode;
	}

	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getRefundMoney() {
		return refundMoney;
	}

	public void setRefundMoney(String refundMoney) {
		this.refundMoney = refundMoney;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getRefundTime() {
		return refundTime;
	}

	public void setRefundTime(String refundTime) {
		this.refundTime = refundTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getBusinessTradeCode() {
		return businessTradeCode;
	}

	public void setBusinessTradeCode(String businessTradeCode) {
		this.businessTradeCode = businessTradeCode;
	}

    
	
}
