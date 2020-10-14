package com.cmall.groupcenter.homehas.model;

import java.io.Serializable;

public class RsyncModelTVInfo implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 节目编号
	 */
	private String form_id="";
	/**
	 * 标题名
	 */
	private String title_nm="";
	/**
	 *节目开始日期 
	 */
	private String form_fr_date = "";
	/**
	 *节目结束日期 
	 */
	private String form_end_date = "";
	/**
	 *节目分类 
	 */
	private String form_cd = "";
	/**
	 *序号 
	 */
	private String form_seq = "";
	/**
	 *频道 
	 */
	private String so_id = "";
	/**
	 *商品编码 
	 */
	private String good_id = "";
	/**
	 *播放展示 
	 */
	private String form_good_mis = "";
	public String getForm_id() {
		return form_id;
	}
	public void setForm_id(String form_id) {
		this.form_id = form_id;
	}
	public String getTitle_nm() {
		return title_nm;
	}
	public void setTitle_nm(String title_nm) {
		this.title_nm = title_nm;
	}
	public String getForm_fr_date() {
		return form_fr_date;
	}
	public void setForm_fr_date(String form_fr_date) {
		this.form_fr_date = form_fr_date;
	}
	
	public String getForm_end_date() {
		return form_end_date;
	}
	public void setForm_end_date(String form_end_date) {
		this.form_end_date = form_end_date;
	}
	public String getForm_cd() {
		return form_cd;
	}
	public void setForm_cd(String form_cd) {
		this.form_cd = form_cd;
	}
	public String getForm_seq() {
		return form_seq;
	}
	public void setForm_seq(String form_seq) {
		this.form_seq = form_seq;
	}
	public String getSo_id() {
		return so_id;
	}
	public void setSo_id(String so_id) {
		this.so_id = so_id;
	}
	public String getGood_id() {
		return good_id;
	}
	public void setGood_id(String good_id) {
		this.good_id = good_id;
	}
	public String getForm_good_mis() {
		return form_good_mis;
	}
	public void setForm_good_mis(String form_good_mis) {
		this.form_good_mis = form_good_mis;
	}
	
}
