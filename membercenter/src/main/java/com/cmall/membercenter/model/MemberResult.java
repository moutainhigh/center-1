package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class MemberResult  extends RootResultWeb {

	
	@ZapcomApi(value = "用户资料")
	private MemberInfo user = new MemberInfo();
	

	@ZapcomApi(value = "用户配置")
	private MemberConfig config = new MemberConfig();
	

	public MemberInfo getUser() {
		return user;
	}

	public void setUser(MemberInfo user) {
		this.user = user;
	}

	public MemberConfig getConfig() {
		return config;
	}

	public void setConfig(MemberConfig config) {
		this.config = config;
	}

}
