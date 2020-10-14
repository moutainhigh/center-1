package com.cmall.newscenter.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 消息 - 报名输出类
 * @author liqiang
 * date 2014-7-21
 * @version 1.0
 */
public class MessageApplyResult extends RootResultWeb{
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();
	
	@ZapcomApi(value = "报名信息")
	private List<ApplyMessage> messages = new ArrayList<ApplyMessage>();
	
	@ZapcomApi(value = "消息未读数量")
	private int unread_count = 0;
	
	public int getUnread_count() {
		return unread_count;
	}

	public void setUnread_count(int unread_count) {
		this.unread_count = unread_count;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	public List<ApplyMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<ApplyMessage> messages) {
		this.messages = messages;
	}

	
	
}
