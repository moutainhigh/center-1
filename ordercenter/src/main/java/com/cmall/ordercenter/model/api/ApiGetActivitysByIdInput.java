package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetActivitysByIdInput extends RootInput {
	
	/**
	 * 卖家code
	 */
	@ZapcomApi(value="活动编号",require=1)
	private String activityCode="";

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

}
