package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 消息 - 标记已读输出类
 * @author liqiang
 * date 2014-7-21
 * @version 1.0
 */
public class MessageUnReadResult extends RootResultWeb{
	
	@ZapcomApi(value="未读消息数",remark="999")
	private String count;

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}
	
}
