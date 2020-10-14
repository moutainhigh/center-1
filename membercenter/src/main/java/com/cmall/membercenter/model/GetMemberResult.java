package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetMemberResult  extends RootResultWeb {

	
	@ZapcomApi(value = "用户资料")
	private MemberInfo user = new MemberInfo();

	public MemberInfo getUser() {
		return user;
	}

	public void setUser(MemberInfo user) {
		this.user = user;
	}
	
	
}
