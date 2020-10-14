package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 消息模块-消息详情
 * @author lipengfei
 * @date 2015-5-29
 * email:lipf@ichsy.com
 *
 */
public class AccountMessageSetReadTypeInput extends RootInput {
	
	@ZapcomApi(value = "消息类型", remark = "消息类型(1:好消息，2:坏消息,3:新好友加入 ,4：所有消息)", demo = "1")
	private String messageType;

	public String getMessageType() {
		return messageType;
	}

	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	
}
