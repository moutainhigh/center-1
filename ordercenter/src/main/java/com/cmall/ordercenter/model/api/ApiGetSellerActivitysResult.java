package com.cmall.ordercenter.model.api;

import java.util.List;

import com.cmall.ordercenter.model.OcActivity;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetSellerActivitysResult extends RootResult{

	
	private List<OcActivity> activityList = null;

	public List<OcActivity> getActivityList() {
		return activityList;
	}

	public void setActivityList(List<OcActivity> activityList) {
		this.activityList = activityList;
	}
	
}
