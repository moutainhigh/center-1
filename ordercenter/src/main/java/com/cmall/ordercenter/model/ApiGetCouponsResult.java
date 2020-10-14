package com.cmall.ordercenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiGetCouponsResult extends RootResultWeb {
	
	@ZapcomApi(value="系统时间")
	private String  systemTime ="";
	
	@ZapcomApi(value="结束时间")
	private String  endTime ="";

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getSystemTime() {
		return systemTime;
	}

	public void setSystemTime(String systemTime) {
		this.systemTime = systemTime;
	}

	

}
