package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class ApplyUser {
	
	@ZapcomApi(value="昵称",demo="Gavin.Kwoe")
	private String nickname="";
	
	@ZapcomApi(value="电话",demo="13133201569")
	private String mobile="";
	
	@ZapcomApi(value="报名时间",demo="2009/07/07 21:51:22")
	private String apply_time="";

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getApply_time() {
		return apply_time;
	}

	public void setApply_time(String apply_time) {
		this.apply_time = apply_time;
	}
	
	
}
