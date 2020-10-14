package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 保存消息-输入类
 * @author wangzx
 * date 2015-6-2
 * @version 1.0
 */
public class ChatMessageInput extends RootInput {
	
	@ZapcomApi(value="发送人用户 Id",remark="发送人用户 Id",demo="123456",require=1)
	private String senderId = "";
	
	@ZapcomApi(value = "接收用户 Id",remark = "接收用户 Id" ,demo= "123456",require = 1)
	private String  receiverId = "";
	
	@ZapcomApi(value = "消息类型",remark = "消息类型 文本消息：RC:TxtMsg 图片消息：RC:ImgMsg 语音消息：RC:VcMsg 图文消息：RC:ImgTextMsg" ,require = 1)
	private String  messType = "";
	
	@ZapcomApi(value = "发送时间",remark = "发送时间")
	private String  sendTime = "";
	
	@ZapcomApi(value = "接受时间",remark = "接受时间")
	private String  receiverTime = "";
	
	@ZapcomApi(value = "发送内容",remark = "发送内容")
	private String  chatContent = "";
	
	@ZapcomApi(value = "发送状态",remark = "发送状态 发送中：10 发送失败：20 已发送：30 对方已接收： 40 对方已读：50 对方已销毁：60" ,require = 1)
	private int  sendStatus =-1;
	
	@ZapcomApi(value = "接收状态",remark = "接收的状态 未读：0 已读：1 语音未听 ：2 已下载：4" )
	private int  receiverStatus =-1;
	
	@ZapcomApi(value = "消息的方向",remark = "消息的方向：1 对应发送 2 对应接收" )
	private int  messDirection =-1;
	
	@ZapcomApi(value = "消息场合",remark = "消息的场合：私聊：1 讨论组：2 群组3 聊天室 4 客服消息 5 系统消息 6")
	private int  messOccasion =-1;

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

	public String getMessType() {
		return messType;
	}

	public void setMessType(String messType) {
		this.messType = messType;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getReceiverTime() {
		return receiverTime;
	}

	public void setReceiverTime(String receiverTime) {
		this.receiverTime = receiverTime;
	}

	public String getChatContent() {
		return chatContent;
	}

	public void setChatContent(String chatContent) {
		this.chatContent = chatContent;
	}

	public int getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(int sendStatus) {
		this.sendStatus = sendStatus;
	}

	public int getReceiverStatus() {
		return receiverStatus;
	}

	public void setReceiverStatus(int receiverStatus) {
		this.receiverStatus = receiverStatus;
	}

	public int getMessDirection() {
		return messDirection;
	}

	public void setMessDirection(int messDirection) {
		this.messDirection = messDirection;
	}

	public int getMessOccasion() {
		return messOccasion;
	}

	public void setMessOccasion(int messOccasion) {
		this.messOccasion = messOccasion;
	}

	

}
