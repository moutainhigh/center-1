package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.topapi.RootResult;

public class AccountConfirmResult extends RootResult {

	private String error = "";
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	
}
