package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncResponse;

public class RsyncResponseIntegralRelation  implements IRsyncResponse {

	/**
	 * 成功标识
	 */
	private String success;
	
	/**
	 * 失败原因
	 */
	private String message;

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
