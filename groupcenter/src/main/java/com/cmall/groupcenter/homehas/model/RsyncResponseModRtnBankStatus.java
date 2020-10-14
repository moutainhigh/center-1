package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncResponse;

public class RsyncResponseModRtnBankStatus implements IRsyncResponse {

	private boolean success;
	private String message;
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
