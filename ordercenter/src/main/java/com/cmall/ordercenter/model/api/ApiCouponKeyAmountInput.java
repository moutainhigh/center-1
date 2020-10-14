package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiCouponKeyAmountInput extends RootInput {
	@ZapcomApi(value="优惠码",remark="优惠码",require=1)
	private String cdkey  = "";
	@ZapcomApi(value="活动编码",remark="活动编码",require=1)
	private String activityCode = "";

	public String getCdkey() {
		return cdkey;
	}

	public void setCdkey(String cdkey) {
		this.cdkey = cdkey;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}
	
}
