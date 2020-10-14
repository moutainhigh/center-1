package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncResponse;

public class RsyncResponseEndMessageUseM implements IRsyncResponse {

	private String send_result = "";

	public String getSend_result() {
		return send_result;
	}

	public void setSend_result(String send_result) {
		this.send_result = send_result;
	}

	private String send_message = "";
	private String result = "";

	public String getSend_message() {
		return send_message;
	}

	public void setSend_message(String send_message) {
		this.send_message = send_message;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
