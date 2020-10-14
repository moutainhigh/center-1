package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class MemberChangeResult extends RootResultWeb{

	
	@ZapcomApi(value="积分变动")
	private ScoredChange scored=new ScoredChange();
	
	@ZapcomApi(value="用户信息")
	private MemberInfo user=new MemberInfo();

	public ScoredChange getScored() {
		return scored;
	}

	public void setScored(ScoredChange scored) {
		this.scored = scored;
	}

	public MemberInfo getUser() {
		return user;
	}

	public void setUser(MemberInfo user) {
		this.user = user;
	}
	
	
	
}
