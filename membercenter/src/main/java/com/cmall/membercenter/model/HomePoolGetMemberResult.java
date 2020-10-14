package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class HomePoolGetMemberResult  extends RootResultWeb {

	
	@ZapcomApi(value = "家有汇用户资料")
	private HomePoolMemberInfo user = new HomePoolMemberInfo();

	public HomePoolMemberInfo getUser() {
		return user;
	}

	public void setUser(HomePoolMemberInfo user) {
		this.user = user;
	}
	
	
}
