package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/** 
* @Author fufu
* @Time 2020年6月29日 下午5:44:30 
* @Version 1.0
* <p>Description:</p>
*/
public class RsyncResponseGetPlusSaveMoney implements IRsyncResponse{
	private boolean success;
	private String message;
	
	private String status;//新添加字段 状态码
	
	private String result;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	
	
}
