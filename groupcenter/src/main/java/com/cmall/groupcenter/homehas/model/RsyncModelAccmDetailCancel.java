package com.cmall.groupcenter.homehas.model;

import java.io.Serializable;

public class RsyncModelAccmDetailCancel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ord_id;
	private String ord_seq;
	private String rtn_cnfm_date;
	
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
	public String getRtn_cnfm_date() {
		return rtn_cnfm_date;
	}
	public void setRtn_cnfm_date(String rtn_cnfm_date) {
		this.rtn_cnfm_date = rtn_cnfm_date;
	}
	
}
