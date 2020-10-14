package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 4.66.积分变化明细(惠家有通路)同步接口的请求参数
 */
public class RsyncRequestGetAccmDetail implements IRsyncRequest {
	
	// 请求开始时间 格式 ： yyyy-MM-dd HH:mm:ss
	private String start_date = "";
	// 请求结束时间 格式 ： yyyy-MM-dd HH:mm:ss
	private String end_date = "";
	
	public String getStart_date() {
		return start_date;
	}
	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}
}
