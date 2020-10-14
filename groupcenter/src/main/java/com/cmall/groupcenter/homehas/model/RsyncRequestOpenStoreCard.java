package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestOpenStoreCard implements IRsyncRequest {

	/**
	 * LD客代号
	 */
	private String cust_id;
	
	/**
	 * LD客代号
	 */
	private String cust_nm;
	
	/**
	 * member_code
	 */
	private String web_id;
	
	/**
	 * 手机号
	 */
	private String mobile;
	
	/**
	 * 储值卡号
	 */
	private String card_no;
	
	/**
	 * 储值卡密码
	 */
	private String card_pwd;
	
	/**
	 * 收货人省市县
	 */
	private String addr_1;
	
	/**
	 * 收货人详细地址
	 */
	private String addr_2;
	
	/**
	 * 收货人行政区域
	 */
	private String srgn_cd;
	
	/**
	 * 邮编
	 */
	private String zip_no;

	public String getCust_nm() {
		return cust_nm;
	}

	public void setCust_nm(String cust_nm) {
		this.cust_nm = cust_nm;
	}

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getWeb_id() {
		return web_id;
	}

	public void setWeb_id(String web_id) {
		this.web_id = web_id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String getCard_pwd() {
		return card_pwd;
	}

	public void setCard_pwd(String card_pwd) {
		this.card_pwd = card_pwd;
	}

	public String getAddr_1() {
		return addr_1;
	}

	public void setAddr_1(String addr_1) {
		this.addr_1 = addr_1;
	}

	public String getAddr_2() {
		return addr_2;
	}

	public void setAddr_2(String addr_2) {
		this.addr_2 = addr_2;
	}

	public String getSrgn_cd() {
		return srgn_cd;
	}

	public void setSrgn_cd(String srgn_cd) {
		this.srgn_cd = srgn_cd;
	}

	public String getZip_no() {
		return zip_no;
	}

	public void setZip_no(String zip_no) {
		this.zip_no = zip_no;
	}

}
