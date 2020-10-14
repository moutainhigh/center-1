package com.cmall.groupcenter.homehas.model;

import java.io.Serializable;

public class RsyncModelAccmDetail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String accm_id;
	private String ord_id;
	private String ord_seq;
	private String good_id;
	private String color_id; 
	private String style_id;
	private String accm_rsn_cd; //积分原因类型
	private String accm_amt; //积分金额，保留三位小数
	private String cnfm_date; //确定时间
	
	public String getAccm_id() {
		return accm_id;
	}
	public void setAccm_id(String accm_id) {
		this.accm_id = accm_id;
	}
	public String getOrd_id() {
		return ord_id;
	}
	public void setOrd_id(String ord_id) {
		this.ord_id = ord_id;
	}
	public String getOrd_seq() {
		return ord_seq;
	}
	public void setOrd_seq(String ord_seq) {
		this.ord_seq = ord_seq;
	}
	public String getGood_id() {
		return good_id;
	}
	public void setGood_id(String good_id) {
		this.good_id = good_id;
	}
	public String getColor_id() {
		return color_id;
	}
	public void setColor_id(String color_id) {
		this.color_id = color_id;
	}
	public String getStyle_id() {
		return style_id;
	}
	public void setStyle_id(String style_id) {
		this.style_id = style_id;
	}
	public String getAccm_rsn_cd() {
		return accm_rsn_cd;
	}
	public void setAccm_rsn_cd(String accm_rsn_cd) {
		this.accm_rsn_cd = accm_rsn_cd;
	}
	public String getAccm_amt() {
		return accm_amt;
	}
	public void setAccm_amt(String accm_amt) {
		this.accm_amt = accm_amt;
	}
	public String getCnfm_date() {
		return cnfm_date;
	}
	public void setCnfm_date(String cnfm_date) {
		this.cnfm_date = cnfm_date;
	}
	
}
