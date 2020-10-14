package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 同步配送的请求参数
 * 
 * @author srnpr
 * 
 */
public class RsyncRequestGetShipmentStat implements IRsyncRequest {

	private String start_time = "";

	private String end_time = "";

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

}
