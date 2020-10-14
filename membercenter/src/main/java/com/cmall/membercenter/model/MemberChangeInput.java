package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class MemberChangeInput extends RootInput {

	
	@ZapcomApi(value = "昵称", require = 0, remark = "昵称,最长30位", demo = "123", verify = { "maxlength=30" })
	private String nickname = "";
	
	
	@ZapcomApi(value = "性别", require = 0, remark = "性别,可选值:4497465100010001(保密)，4497465100010002(男),4497465100010003(女)。", demo = "4497465100010001", verify = { "in=4497465100010001,4497465100010002,4497465100010003" })
	private String gender = "";


	public String getNickname() {
		return nickname;
	}


	public void setNickname(String nickname) {
		this.nickname = nickname;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}
	
	
}
