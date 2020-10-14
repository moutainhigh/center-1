package com.cmall.groupcenter.homehas.model;

import java.util.List;

public class FormResult {

	/**1为本档节目  2为上一档节目*/
	private String form_type;
	private String form_id;
	private String form_fr_date;
	private String form_end_date;
	private String form_title;
	private String form_cd;
	private String form_good_mis;
	private List<GoodForScanCode> goodsList;
	
	public String getForm_type() {
		return form_type;
	}
	public void setForm_type(String form_type) {
		this.form_type = form_type;
	}
	public String getForm_id() {
		return form_id;
	}
	public void setForm_id(String form_id) {
		this.form_id = form_id;
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
	public String getForm_title() {
		return form_title;
	}
	public void setForm_title(String form_title) {
		this.form_title = form_title;
	}
	public String getForm_cd() {
		return form_cd;
	}
	public void setForm_cd(String form_cd) {
		this.form_cd = form_cd;
	}
	public String getForm_good_mis() {
		return form_good_mis;
	}
	public void setForm_good_mis(String form_good_mis) {
		this.form_good_mis = form_good_mis;
	}
	public List<GoodForScanCode> getGoodsList() {
		return goodsList;
	}
	public void setGoodsList(List<GoodForScanCode> goodsList) {
		this.goodsList = goodsList;
	}
	
	
}
