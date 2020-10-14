package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 暂存款、储备金 使用响应信息
 * @author pang_jhui
 *
 */
public class RsyncEmployAccountResponse implements IRsyncResponse {
	
	/*是否成功*/
	private boolean success;
	
	/*返回信息*/
	private String message = "";

	public boolean getSuccess() {
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
