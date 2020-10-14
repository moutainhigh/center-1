package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/** 
* @Author fufu
* @Time 2020年6月29日 下午5:44:30 
* @Version 1.0
* <p>Description:</p>
*/
public class RsyncRequestGetPlusSaveMoney implements IRsyncRequest{

	/**
	 * 活动id
	 */
	private String event_id = "";
	/**
	 * 用户ID
	 */
	private String cust_id = "";
	public String getEvent_id() {
		return event_id;
	}
	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	
}
