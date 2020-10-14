package com.cmall.newscenter.beauty.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 系统 - 修改消息状态输入类
 * @author houwen	
 * date 2014-9-29
 * @version 1.0
 */
public class MessageStatusInput extends RootInput{

	@ZapcomApi(value="消息Code",remark="消息Code",demo="XX140909100003,XX140909100002",require=1)
	private String message_code = "";

	public String getMessage_code() {
		return message_code;
	}

	public void setMessage_code(String message_code) {
		this.message_code = message_code;
	}
	
	
}
