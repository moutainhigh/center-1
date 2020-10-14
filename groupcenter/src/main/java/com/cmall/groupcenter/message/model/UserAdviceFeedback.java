package com.cmall.groupcenter.message.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class UserAdviceFeedback extends RootResultWeb {
	@ZapcomApi(value="消息列表",remark="消息列表")
	private List<UserAdviceFeedbackResult> list = new ArrayList<UserAdviceFeedbackResult>();

	public List<UserAdviceFeedbackResult> getList() {
		return list;
	}

	public void setList(List<UserAdviceFeedbackResult> list) {
		this.list = list;
	}
}