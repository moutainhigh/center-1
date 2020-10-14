package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * ClassName: OcBoutiqueMarket <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * date: 2013年10月28日 下午6:43:34 <br/>
 * @author hxd
 * @version 
 * @since JDK 1.6
 */
public class OcBoutiqueMarket extends BaseClass
{
	/**
	 * 精品汇名称
	 */
	private String boutique_name = "";
	/**
	 * 精品汇代码
	 */
	private String boutique_code = "";
	
	
	/**
	 * 精品汇备注信息
	 */
	
	private String description = "";

	/**
	 * 活动开始时间
	 */
	private String start_time = "";
	/**
	 * 活动结束时间
	 */
	private String end_time = "";
	
	
	
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


	public OcBoutiqueMarket(String boutique_name, String boutique_code,
			String description, String start_time, String end_time) {
		super();
		this.boutique_name = boutique_name;
		this.boutique_code = boutique_code;
		this.description = description;
		this.start_time = start_time;
		this.end_time = end_time;
	}


	public OcBoutiqueMarket() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
