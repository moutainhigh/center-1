package com.cmall.groupcenter.homehas.model;

public class RsyncModelRegularToNewRelation {

	/**
	 * 老用户惠家有编号
	 */
	private String web_id;
	
	/**
	 * 新用户惠家有编号
	 */
	private String scust_web_id;
	
	/**
	 * 老用户手机号
	 */
	private String fcust_id;
	
	/**
	 * 新用户手机号
	 */
	private String scust_id;
	
	/**
	 * 创建人
	 */
	private String etr_id;
	
	/**
	 * 创建时间 yyyy-MM-DD HH24:mm:ss
	 */
	private String etr_date;

	public String getWeb_id() {
		return web_id;
	}

	public void setWeb_id(String web_id) {
		this.web_id = web_id;
	}

	public String getScust_web_id() {
		return scust_web_id;
	}

	public void setScust_web_id(String scust_web_id) {
		this.scust_web_id = scust_web_id;
	}

	public String getFcust_id() {
		return fcust_id;
	}

	public void setFcust_id(String fcust_id) {
		this.fcust_id = fcust_id;
	}

	public String getScust_id() {
		return scust_id;
	}

	public void setScust_id(String scust_id) {
		this.scust_id = scust_id;
	}

	public String getEtr_id() {
		return etr_id;
	}

	public void setEtr_id(String etr_id) {
		this.etr_id = etr_id;
	}

	public String getEtr_date() {
		return etr_date;
	}

	public void setEtr_date(String etr_date) {
		this.etr_date = etr_date;
	}
	
	
	
}
