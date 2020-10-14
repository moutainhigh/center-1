package com.cmall.productcenter.model;

public class Boutique {
	/**
	 * 精品汇名称
	 */
	private String boutique_name;
	/**
	 * 精品汇Code
	 */
	private String boutique_code;
	/**
	 * 精品汇描述
	 */
	private String description;
	/**
	 * 开始时间
	 */
	private String start_time;
	/**
	 * 开始时间
	 */
	private String end_time;
	/**
	 * 图片url
	 */
	private String pic_url;
	public String getBoutique_name() {
		return boutique_name;
	}
	public void setBoutique_name(String boutique_name) {
		this.boutique_name = boutique_name;
	}
	public String getBoutique_code() {
		return boutique_code;
	}
	public void setBoutique_code(String boutique_code) {
		this.boutique_code = boutique_code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getPic_url() {
		return pic_url;
	}
	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}
	public Boutique(String boutique_name, String boutique_code,
			String description, String start_time, String end_time,
			String pic_url) {
		super();
		this.boutique_name = boutique_name;
		this.boutique_code = boutique_code;
		this.description = description;
		this.start_time = start_time;
		this.end_time = end_time;
		this.pic_url = pic_url;
	}
	public Boutique() {
		super();
	}
}
