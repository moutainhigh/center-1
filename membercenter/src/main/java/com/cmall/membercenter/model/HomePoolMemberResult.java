package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class HomePoolMemberResult  extends RootResultWeb {

	
	@ZapcomApi(value = "用户资料")
	private HomePoolMemberInfo user = new HomePoolMemberInfo();
	

	@ZapcomApi(value = "用户配置")
	private MemberConfig config = new MemberConfig();
	

	public HomePoolMemberInfo getUser() {
		return user;
	}

	public void setUser(HomePoolMemberInfo user) {
		this.user = user;
	}

	public MemberConfig getConfig() {
		return config;
	}

	public void setConfig(MemberConfig config) {
		this.config = config;
	}

}
