package com.cmall.groupcenter.homehas.model;

import java.io.Serializable;

public class RsyncModelDlvPay implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 大中小区域编码
	 */
	private String lrgn_cd = "";
	private String mrgn_cd = "";
	private String srgn_cd = "";
	
	public String getLrgn_cd() {
		return lrgn_cd;
	}
	public void setLrgn_cd(String lrgn_cd) {
		this.lrgn_cd = lrgn_cd;
	}
	public String getMrgn_cd() {
		return mrgn_cd;
	}
	public void setMrgn_cd(String mrgn_cd) {
		this.mrgn_cd = mrgn_cd;
	}
	public String getSrgn_cd() {
		return srgn_cd;
	}
	public void setSrgn_cd(String srgn_cd) {
		this.srgn_cd = srgn_cd;
	}
	
}
