package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncResponse;

public class RsyncResponseProductForScanCode implements IRsyncResponse {

	private boolean success;
	private FormResult[] result;
	public boolean getSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public FormResult[] getResult() {
		return result;
	}
	public void setResult(FormResult[] result) {
		this.result = result;
	}

	
}
