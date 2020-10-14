package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class GroupReconciliationInput extends RootInput{
	
	@ZapcomApi(value = "开始时间",remark = "2015-04-15 14:34:23", require = 1,verify="base=datetime")
	String startTime="";
	
	@ZapcomApi(value = "结束时间",remark = "2015-04-15 14:34:23", require = 1,verify="base=datetime")
	String endTime="";
	
	@ZapcomApi(value = "账单类型",remark = "支付单-4497465200200001，退款单-4497465200200002,为空默认查询全部", require = 0)
	String type="";

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	

}
