package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetCouponsInput  extends RootInput {
	
	@ZapcomApi(value = "优惠券UID", require = 1, remark = "UID")
	private String uid="";
	
	@ZapcomApi(value = "优惠券类型编号", require = 1, remark = "couponTypeCode")
	private String couponTypeCode="";

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCouponTypeCode() {
		return couponTypeCode;
	}

	public void setCouponTypeCode(String couponTypeCode) {
		this.couponTypeCode = couponTypeCode;
	}
	
	
}
