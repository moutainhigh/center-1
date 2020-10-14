package com.cmall.groupcenter.groupapp.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class GetInvitedPhoneUserInput extends RootInput{
	
	@ZapcomApi(value = "手机号",require=1,remark="逗号隔开",demo="15810241269,15810241263")
	private String phones= "";

	public String getPhones() {
		return phones;
	}

	public void setPhones(String phones) {
		this.phones = phones;
	}
	
}
