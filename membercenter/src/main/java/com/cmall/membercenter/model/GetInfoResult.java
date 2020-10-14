package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetInfoResult extends RootResultWeb {

	
	@ZapcomApi(value="用户信息")
	private MemberInfo user=new MemberInfo();

	public MemberInfo getUser() {
		return user;
	}

	public void setUser(MemberInfo user) {
		this.user = user;
	}
	
	
	
}
