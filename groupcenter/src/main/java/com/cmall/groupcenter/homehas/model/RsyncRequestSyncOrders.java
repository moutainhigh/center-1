package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 批量同步会员的请求参数
 * 
 * @author srnpr
 * 
 */
public class RsyncRequestSyncOrders implements IRsyncRequest {

	private String start_date = "";

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
