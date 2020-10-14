package com.cmall.newscenter.beauty.model;


import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 系统 - 消息 输出类
 * @author houwen	
 * date 2014-9-15
 * @version 1.0
 */
public class MessageSystemResult extends RootResultWeb{
	
		
	@ZapcomApi(value = "系统信息")
	private List<SystemMessage> messages = new ArrayList<SystemMessage>();

	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();
	
	
	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	public List<SystemMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<SystemMessage> messages) {
		this.messages = messages;
	}
	

}
