package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 惠美丽—护肤需求实体类
 * 
 * @author yangrong date: 2014-12-05
 * @version1.3.0
 */
public class SkinHopeful {

	@ZapcomApi(value = "护肤需求code")
	private String hopeful_code = "";

	@ZapcomApi(value = "护肤需求名称")
	private String hopeful_name = "";

	public String getHopeful_code() {
		return hopeful_code;
	}

	public void setHopeful_code(String hopeful_code) {
		this.hopeful_code = hopeful_code;
	}

	public String getHopeful_name() {
		return hopeful_name;
	}

	public void setHopeful_name(String hopeful_name) {
		this.hopeful_name = hopeful_name;
	}
	
	
}
