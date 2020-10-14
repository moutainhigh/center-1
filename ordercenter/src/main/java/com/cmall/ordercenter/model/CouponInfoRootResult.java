package com.cmall.ordercenter.model;

import java.math.BigDecimal;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.helper.MoneyHelper;

public class CouponInfoRootResult extends RootResult{
	
	@ZapcomApi(value="优惠劵编号",remark="优惠劵编号")
	private String couponCode="";
	
	@ZapcomApi(value="剩余金额",remark="剩余金额")
	private BigDecimal surplusMoney=new BigDecimal(0);

	@ZapcomApi(value="开始时间",remark="开始时间")
	private String startTime="";
	
	@ZapcomApi(value="结束时间",remark="结束时间")
	private String endTime="";
	
	@ZapcomApi(value="优惠劵状态",remark="0：未使用；1：已使用；2：已过期",demo="1")
	private int status=0;
	
	@ZapcomApi(value="使用范围",remark="使用范围")
	private String useRange="";

	@ZapcomApi(value="使用限制",remark="使用限制")
	private String useLimit="";

	@ZapcomApi(value="最小限额",remark="最小限额")
	private BigDecimal limitMoney=new BigDecimal(0);
	
	@ZapcomApi(value="优惠券数量",remark="")
	private int count=1;
		
	public String getUseRange() {
		return useRange;
	}

	public void setUseRange(String useRange) {
		this.useRange = useRange;
	}

	public String getUseLimit() {
		return useLimit;
	}

	public void setUseLimit(String useLimit) {
		this.useLimit = useLimit;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}



	public BigDecimal getSurplusMoney() {
		return this.surplusMoney;
	}

	public void setSurplusMoney(BigDecimal surplusMoney) {
		this.surplusMoney = new BigDecimal(MoneyHelper.format(surplusMoney)) ;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public BigDecimal getLimitMoney() {
		return this.limitMoney;
	}

	public void setLimitMoney(BigDecimal limitMoney) {
		this.limitMoney = new BigDecimal(MoneyHelper.format(limitMoney));
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	

}
