package com.cmall.newscenter.model;

import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ChartMessageListResult extends RootResultWeb {
	
	@ZapcomApi(value = "消息列表",remark="消息列表")
	private List<ChatMessageInput> list =null;

	public List<ChatMessageInput> getList() {
		return list;
	}

	public void setList(List<ChatMessageInput> list) {
		this.list = list;
	}
}
