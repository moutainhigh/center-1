package com.cmall.newscenter.beauty.model;


import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 消息 - 与我相关 输出类
 * @author houwen	
 * date 2014-9-16
 * @version 1.0
 */
public class MessageAboutMeResult extends RootResultWeb{
	
		
	@ZapcomApi(value = "系统信息")
	private List<AboutMeMessage> messages = new ArrayList<AboutMeMessage>();

	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();
	
	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	public List<AboutMeMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<AboutMeMessage> messages) {
		this.messages = messages;
	}


}
