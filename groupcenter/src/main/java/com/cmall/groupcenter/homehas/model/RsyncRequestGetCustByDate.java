package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 批量同步会员的请求参数
 * 
 * @author srnpr
 * 
 */
public class RsyncRequestGetCustByDate implements IRsyncRequest {

	private String start_day = "";

	private String end_day = "";

	public String getStart_day() {
		return start_day;
	}

	public void setStart_day(String start_day) {
		this.start_day = start_day;
	}

	public String getEnd_day() {
		return end_day;
	}

	public void setEnd_day(String end_day) {
		this.end_day = end_day;
	}

}
