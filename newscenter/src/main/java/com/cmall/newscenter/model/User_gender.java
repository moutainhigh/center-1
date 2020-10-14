package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class User_gender {

	@ZapcomApi(value = "保密",demo="4497465100010001")
	private String unknown ="";

	@ZapcomApi(value = "男",demo="4497465100010002")
	private String male ="";
	
	@ZapcomApi(value = "女",demo="4497465100010003")
	private String female ="";

	public String getUnknown() {
		return unknown;
	}

	public void setUnknown(String unknown) {
		this.unknown = unknown;
	}

	public String getMale() {
		return male;
	}

	public void setMale(String male) {
		this.male = male;
	}

	public String getFemale() {
		return female;
	}

	public void setFemale(String female) {
		this.female = female;
	}
	
	
}
