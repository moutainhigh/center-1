package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncResponse;

public class RsyncResponseAlipayMoveInformation implements IRsyncResponse{
	
	private boolean send_result; //处理结果 True/false
	private String send_message;  //处理结果描述
	
	public String getSend_message() {
		return send_message;
	}
	public void setSend_message(String send_message) {
		this.send_message = send_message;
	}
	public boolean isSend_result() {
		return send_result;
	}
	public void setSend_result(boolean send_result) {
		this.send_result = send_result;
	}
}
