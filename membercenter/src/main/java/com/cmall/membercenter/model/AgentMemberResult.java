package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class AgentMemberResult  extends RootResultWeb {

	
	@ZapcomApi(value = "用户资料")
	private AgentMemberInfo user = new AgentMemberInfo();

	public AgentMemberInfo getUser() {
		return user;
	}

	public void setUser(AgentMemberInfo user) {
		this.user = user;
	}
	

}
