package com.cmall.groupcenter.account.model;

import java.util.List;

import com.cmall.groupcenter.model.PageResults;
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
public class AccountMessageListResult extends RootResultWeb {
	
	@ZapcomApi(value = "消息列表", remark = "消息列表")
	List<AccountMessageResult> messageList;


	public List<AccountMessageResult> getMessageList() {
		return messageList;
	}

	public void setMessageList(List<AccountMessageResult> messageList) {
		this.messageList = messageList;
	}
	
}
