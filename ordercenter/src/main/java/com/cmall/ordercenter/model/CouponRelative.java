package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class CouponRelative {
	@ZapcomApi(value="连续签到天数",remark="连续签到天数")
	private Integer signSeqDays;
	
	@ZapcomApi(value="满足条件可获得的优惠券名称",remark="满足条件可获得的优惠券名称")
	private String couponName;
	
	@ZapcomApi(value="满足条件可获得的优惠券编码",remark="满足条件可获得的优惠券编码")
	private String couponCode;
	

	@ZapcomApi(value="满足条件可获得的活动编码",remark="满足条件可获得的活动编码")
	private String activityCode;

	public String getCouponCode() {
		return couponCode;
	}
	
	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}
	
	public Integer getSignSeqDays() {
		return signSeqDays;
	}

	public void setSignSeqDays(Integer signSeqDays) {
		this.signSeqDays = signSeqDays;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}
	
	
	
	
}
