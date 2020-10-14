package com.cmall.groupcenter.mq.model;

/**
 * 
 * @remark 活动信息
 * @author 任宏斌
 * @date 2018年9月17日
 */
public class CustLvlListenModel {

	/**
	 * 客代号
	 */
	private String cust_id;

	/**
	 * 客户等级
	 */
	private String cust_lvl_cd;
	
	/**
	 * 错误、异常信息
	 */
	private String message;

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getCust_lvl_cd() {
		return cust_lvl_cd;
	}

	public void setCust_lvl_cd(String cust_lvl_cd) {
		this.cust_lvl_cd = cust_lvl_cd;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}