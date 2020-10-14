package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 积分、暂存款、储值金使用接口（新）响应信息
 */
public class RsyncResponseCtrlAccmCrdtPpcServer implements IRsyncResponse {

	/* 是否成功 */
	private boolean success;
	/* 返回信息 */
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
