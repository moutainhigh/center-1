package com.cmall.usercenter.model.api;

import com.srnpr.zapcom.topapi.RootInput;

public class ApiUserInput extends RootInput {
	
	private String userName = "";

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	
	
}
