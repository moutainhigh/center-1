package com.cmall.groupcenter.callapi.input;

import com.srnpr.zapcom.baseface.IBaseInput;

public class ApiBaiDuPushInput implements IBaseInput {
	
	private String toPage;
	private String msgContent;
	private String phone;
	
	public String getToPage() {
		return toPage;
	}
	public void setToPage(String toPage) {
		this.toPage = toPage;
	}
	public String getMsgContent() {
		return msgContent;
	}
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
}
