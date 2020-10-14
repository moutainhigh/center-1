package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 *  查询消息-输入类
 * @author wangzx
 * date 2015-6-30
 * @version 1.0
 */
public class ChatMessageQueryInput extends RootInput {
	
	

	@ZapcomApi(value="发送人用户 Id",remark="发送人用户 Id",demo="123456",require=1)
	private String senderId = "";
	
	@ZapcomApi(value = "接收用户 Id",remark = "接收用户 Id" ,demo= "123456",require = 1)
	private String  receiverId = "";
	
	@ZapcomApi(value = "时间戳",remark = "接收用户 Id" ,require = 1)
	long timestamp;
	
	@ZapcomApi(value = "条数",remark = "查询条数" ,require = 1)
	int qsize;
	
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

	public int getQsize() {
		return qsize;
	}

	public void setQsize(int qsize) {
		this.qsize = qsize;
	}
}
