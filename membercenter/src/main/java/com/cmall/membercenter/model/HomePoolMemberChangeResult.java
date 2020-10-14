package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class HomePoolMemberChangeResult extends RootResultWeb{

	
//	@ZapcomApi(value="积分变动")
//	private ScoredChange scored=new ScoredChange();
	
	@ZapcomApi(value="用户信息")
	private HomePoolMemberInfo user = new HomePoolMemberInfo();

//	public ScoredChange getScored() {
//		return scored;
//	}
//
//	public void setScored(ScoredChange scored) {
//		this.scored = scored;
//	}

	public HomePoolMemberInfo getUser() {
		return user;
	}

	public void setUser(HomePoolMemberInfo user) {
		this.user = user;
	}
	
	
	
}
