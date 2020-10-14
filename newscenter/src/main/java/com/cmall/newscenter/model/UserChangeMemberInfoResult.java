package com.cmall.newscenter.model;


import com.cmall.membercenter.model.MemberInfo;
import com.cmall.membercenter.model.ScoredChange;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 用户 - 修改资料
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class UserChangeMemberInfoResult extends RootResultWeb{
	
	@ZapcomApi(value= "获取积分")
	private ScoredChange scored = new ScoredChange();

	@ZapcomApi(value= "更新后的用户资料")
	private MemberInfo user = new MemberInfo();

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
