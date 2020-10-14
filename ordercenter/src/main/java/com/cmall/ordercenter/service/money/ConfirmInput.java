package com.cmall.ordercenter.service.money;

import com.srnpr.zapcom.topapi.RootInput;

public class ConfirmInput extends RootInput{
	private String name= "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
}
