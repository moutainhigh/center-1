package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
// * 消息 - 标记已读输入类
 * @author liqiang
 * date 2014-7-21
 * @version 1.0
 */
public class MessageReadInput extends RootInput{
	
	@ZapcomApi(value="Message.id",remark="1",require=1)
	private String message="";

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
