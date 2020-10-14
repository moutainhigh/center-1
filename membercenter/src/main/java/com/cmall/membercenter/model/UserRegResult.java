package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class UserRegResult extends MemberResult {

	@ZapcomApi(value = "用户认证串", remark = "注册成功后返回非空，同登陆成功的token")
	private String user_token = "";

	@ZapcomApi(value = "积分变动")
	private ScoredChange scored = new ScoredChange();

	public ScoredChange getScored() {
		return scored;
	}

	public void setScored(ScoredChange scored) {
		this.scored = scored;
	}

	public String getUser_token() {
		return user_token;
	}

	public void setUser_token(String user_token) {
		this.user_token = user_token;
	}

}
