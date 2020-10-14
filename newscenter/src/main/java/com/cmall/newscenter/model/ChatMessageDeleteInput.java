package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 *  删除消息-输入类
 * @author wangzx
 * date 2015-6-30
 * @version 1.0
 */
public class ChatMessageDeleteInput extends RootInput {
	
	public String getSenderId() {
		return senderId;
	}

	public void setSenderId(String senderId) {
		this.senderId = senderId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@ZapcomApi(value="发送人用户 Id",remark="发送人用户 Id",demo="123456",require=1)
	private String senderId = "";
	
	@ZapcomApi(value = "接收用户 Id",remark = "接收用户 Id" ,demo= "123456",require = 1)
	private String  receiverId = "";
	
	@ZapcomApi(value = "时间戳",remark = "时间戳" ,require = 1)
	long timestamp;
}
