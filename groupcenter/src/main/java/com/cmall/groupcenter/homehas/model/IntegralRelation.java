package com.cmall.groupcenter.homehas.model;

public class IntegralRelation {

	/**
	 * 邀请人member_code
	 */
	private String web_id;
	
	/**
	 * 被邀请人member_code
	 */
	private String scust_web_id;
	
	/**
	 * 邀请人手机号
	 */
	private String fcust_id;
	
	/**
	 * 被邀请人手机号
	 */
	private String scust_id;
	
	/**
	 * 是否有效
	 */
	private String vl_yn;
	
	/**
	 * 是否主记录 1是 2否
	 */
	private String is_main;
	
	/**
	 * 创建人
	 */
	private String etr_id = "app";
	
	/**
	 * 创建时间
	 */
	private String etr_date;
	
	/**
	 * 邀请人名称
	 */
	private String cust_nm;
	
	/**
	 * 被邀请人名称
	 */
	private String scust_nm;
	
	/**
	 * 邀请人身份证号
	 */
	private String citi_no;
	
	/**
	 * 被邀请人身份证号
	 */
	private String scust_citi_no;
	
	/**
	 * 邀请人详细地址
	 */
	private String addr_2;
	
	/**
	 * 被邀请人详细地址
	 */
	private String scust_addr_2;
	
	/**
	 * 邀请人地址行政区划
	 */
	private String srgn_cd;
	
	/**
	 * 被邀请人地址行政区划
	 */
	private String scust_srgn_cd;
	/**
	 * 邀请人邮编
	 */
	private String zip_no;
	
	/**
	 * 被邀请人邮编
	 */
	private String scust_zip_no;
	
	/**
	 * 活动编号
	 */
	private String event_code;
	
	/**
	 * 活动开始时间
	 */
	private String begin_time;
	
	/**
	 * 活动结束时间
	 */
	private String end_time;

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

	public String getVl_yn() {
		return vl_yn;
	}

	public void setVl_yn(String vl_yn) {
		this.vl_yn = vl_yn;
	}

	public String getIs_main() {
		return is_main;
	}

	public void setIs_main(String is_main) {
		this.is_main = is_main;
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

	public String getCust_nm() {
		return cust_nm;
	}

	public void setCust_nm(String cust_nm) {
		this.cust_nm = cust_nm;
	}

	public String getScust_nm() {
		return scust_nm;
	}

	public void setScust_nm(String scust_nm) {
		this.scust_nm = scust_nm;
	}

	public String getCiti_no() {
		return citi_no;
	}

	public void setCiti_no(String citi_no) {
		this.citi_no = citi_no;
	}

	public String getScust_citi_no() {
		return scust_citi_no;
	}

	public void setScust_citi_no(String scust_citi_no) {
		this.scust_citi_no = scust_citi_no;
	}

	public String getAddr_2() {
		return addr_2;
	}

	public void setAddr_2(String addr_2) {
		this.addr_2 = addr_2;
	}

	public String getScust_addr_2() {
		return scust_addr_2;
	}

	public void setScust_addr_2(String scust_addr_2) {
		this.scust_addr_2 = scust_addr_2;
	}

	public String getSrgn_cd() {
		return srgn_cd;
	}

	public void setSrgn_cd(String srgn_cd) {
		this.srgn_cd = srgn_cd;
	}

	public String getScust_srgn_cd() {
		return scust_srgn_cd;
	}

	public void setScust_srgn_cd(String scust_srgn_cd) {
		this.scust_srgn_cd = scust_srgn_cd;
	}

	public String getZip_no() {
		return zip_no;
	}

	public void setZip_no(String zip_no) {
		this.zip_no = zip_no;
	}

	public String getScust_zip_no() {
		return scust_zip_no;
	}

	public void setScust_zip_no(String scust_zip_no) {
		this.scust_zip_no = scust_zip_no;
	}

	public String getEvent_code() {
		return event_code;
	}

	public void setEvent_code(String event_code) {
		this.event_code = event_code;
	}

	public String getBegin_time() {
		return begin_time;
	}

	public void setBegin_time(String begin_time) {
		this.begin_time = begin_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

}
