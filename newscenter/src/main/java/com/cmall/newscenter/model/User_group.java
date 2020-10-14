package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class User_group {

	@ZapcomApi(value = "部分用户",demo="4497465000020001")
	private String member ="";

	@ZapcomApi(value = "粉丝头",demo="4497465000020002")
	private String organizer ="";
	
	@ZapcomApi(value = "管理员",demo="4497465000020003")
	private String admin ="";

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public String getOrganizer() {
		return organizer;
	}

	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}

	public String getAdmin() {
		return admin;
	}

	public void setAdmin(String admin) {
		this.admin = admin;
	}
	
}
