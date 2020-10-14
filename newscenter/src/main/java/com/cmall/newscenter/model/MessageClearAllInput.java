package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 消息 - 清空输入类
 * @author liqiang
 * date 2014-7-21
 * @version 1.0
 */
public class MessageClearAllInput extends RootInput{
	
	@ZapcomApi(value = "消息类型",remark = "0" ,demo= "0,1,2",require = 1)
	private int message_type ;
	
	@ZapcomApi(value = "消息编号",remark = "0" ,demo= "xx1232435")
	private String message_code = "";

	public int getMessage_type() {
		return message_type;
	}

	public void setMessage_type(int message_type) {
		this.message_type = message_type;
	}

	public String getMessage_code() {
		return message_code;
	}

	public void setMessage_code(String message_code) {
		this.message_code = message_code;
	}
	
}
