package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 请求参数
 * 
 * @author xiegj
 * 
 */
public class RequestGoodGift implements IRsyncRequest {

	private String subsystem = "app";

	private String good_id = "";
	
	private String site_no = "";

	public String getSubsystem() {
		return subsystem;
	}

	public void setSubsystem(String subsystem) {
		this.subsystem = subsystem;
	}

	public String getGood_id() {
		return good_id;
	}

	public void setGood_id(String good_id) {
		this.good_id = good_id;
	}

	public String getSite_no() {
		return site_no;
	}

	public void setSite_no(String site_no) {
		this.site_no = site_no;
	}

}
