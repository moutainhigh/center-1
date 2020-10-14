package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 消息模块-消息列表接口返回集
 * @author lipengfei
 * @date 2015-5-29
 * email:lipf@ichsy.com
 *
 */
public class AccountMessageResult{
		
	@ZapcomApi(value = "头像", remark = "头像url", demo = "http://convert.com/img/potrait.jpg")
	private String headPotraitUrl;	
	
	@ZapcomApi(value = "消息免打扰状态", remark = "消息免打扰状态(0:开启,1:关闭)", demo = "1")
	private String messageNotifactionStatus;	
	
	@ZapcomApi(value = "消息类型", remark = "消息类型(1:好消息，2:坏消息),只有当输入参数的messageListType=1时，才会分类返回这三种消息。否则只有一种消息", demo = "1")
	private String messageType;
	
	@ZapcomApi(value = "消息标题", remark = "消息标题(标题是指消息详情列表的每一条消息的消息标题，而此处消息列表则指的是当前消息类型的第一条消息的消息标题)", demo = "下单通知撒")
	private String messageTitle;
	
	@ZapcomApi(value = "消息内容", remark = "消息内容,有多条消息内容时，显示最后一条", demo = "")
	private String messageContent;
	
	@ZapcomApi(value = "消息数量", remark = "当前类型的消息的未读的消息数量", demo = "")
	private String messageQuantity;
	
	@ZapcomApi(value = "消息时间", remark = "时间戳", demo = "1433236913000")
	private String messageDate;


	public String getHeadPotraitUrl() {
		return headPotraitUrl;
	}


	public void setHeadPotraitUrl(String headPotraitUrl) {
		this.headPotraitUrl = headPotraitUrl;
	}

	

	public String getMessageNotifactionStatus() {
		return messageNotifactionStatus;
	}


	public void setMessageNotifactionStatus(String messageNotifactionStatus) {
		this.messageNotifactionStatus = messageNotifactionStatus;
	}


	public String getMessageContent() {
		return messageContent;
	}


	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}



	public String getMessageDate() {
		return messageDate;
	}


	public void setMessageDate(String messageDate) {
		this.messageDate = messageDate;
	}


	public String getMessageQuantity() {
		return messageQuantity;
	}


	public void setMessageQuantity(String messageQuantity) {
		this.messageQuantity = messageQuantity;
	}


	public String getMessageTitle() {
		return messageTitle;
	}


	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle;
	}


	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
}
