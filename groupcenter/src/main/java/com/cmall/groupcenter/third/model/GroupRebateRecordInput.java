package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 第三方返利对账输入参数
 * @author chenbin
 *
 */
public class GroupRebateRecordInput extends RootInput{

	@ZapcomApi(value = "开始时间",remark = "2015-04-15 14:34:23", require = 1,verify="base=datetime")
	String startTime="";
	
	@ZapcomApi(value = "结束时间",remark = "2015-04-15 14:34:23", require = 1,verify="base=datetime")
	String endTime="";

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
}
