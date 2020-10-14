package com.cmall.groupcenter.groupapp.model;

import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetInvitedPhoneUserResult extends RootResultWeb {
	
	@ZapcomApi(value = "已邀请(未注册)",remark="逗号隔开")
	private String invitedList;
	@ZapcomApi(value = "未邀请(未注册)",remark="逗号隔开")
	private String uninvitedList;
	
	
	public String getInvitedList() {
		return invitedList;
	}
	public String getUninvitedList() {
		return uninvitedList;
	}
	public void setInvitedList(String invitedList) {
		this.invitedList = invitedList;
	}
	public void setUninvitedList(String uninvitedList) {
		this.uninvitedList = uninvitedList;
	}
	
	

    
	
	
}
