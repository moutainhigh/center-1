package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 查询客户积分、储值金、暂存款查询接口返回数据
 */
public class RsyncResponseGetCustExpireAccm implements IRsyncResponse {

	private boolean success;
	private String message;
	private String result; 
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
