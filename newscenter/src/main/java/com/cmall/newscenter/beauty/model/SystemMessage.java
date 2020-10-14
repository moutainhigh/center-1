package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 系统消息类
 * 2014/09/15
 * @author houwen
 */
public class SystemMessage {

	@ZapcomApi(value = "系统消息编码",remark="1")
	private String message_code  = "";
	
	@ZapcomApi(value = "消息类型",remark="例通知:449746910002;小编提醒:449746910001")
	private String message_type  = "";
	
	@ZapcomApi(value = "消息内容",remark="回复消息")
	private String message_info  = "";

	@ZapcomApi(value = "已读-1，未读-0",remark="1",demo= "0,1")
	private String is_read ;

	@ZapcomApi(value = "发送时间",remark="2009/07/07 21:51:22")
	private String send_time = "";

	public String getMessage_code() {
		return message_code;
	}

	public void setMessage_code(String message_code) {
		this.message_code = message_code;
	}

	public String getMessage_type() {
		return message_type;
	}

	public void setMessage_type(String message_type) {
		this.message_type = message_type;
	}

	public String getMessage_info() {
		return message_info;
	}

	public void setMessage_info(String message_info) {
		this.message_info = message_info;
	}


	public String getIs_read() {
		return is_read;
	}

	public void setIs_read(String is_read) {
		this.is_read = is_read;
	}

	public String getSend_time() {
		return send_time;
	}

	public void setSend_time(String send_time) {
		this.send_time = send_time;
	}

	
}
