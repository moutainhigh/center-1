package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 消息 - 全部已读输入类
 * @author liqiang
 * date 2014-7-21
 * @version 1.0
 */
public class MessageReadAllInput extends RootInput{
	/**
	 * @author yangrong
	 */
	@ZapcomApi(value = "消息类型",remark = "0" ,demo= "0,1,2",require = 1)
	private int message_type ;

	public int getMessage_type() {
		return message_type;
	}

	public void setMessage_type(int message_type) {
		this.message_type = message_type;
	}
	
	
	
	
	
}
