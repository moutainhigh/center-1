package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class UserLoginResult extends MemberResult {

	@ZapcomApi(value = "用户认证串", remark = "登陆成功后返回非空，用于需要用户授权api_token的操作")
	private String user_token = "";
	
	@ZapcomApi(value="积分变动")
	private ScoredChange sChange = new ScoredChange();

	public String getUser_token() {
		return user_token;
	}

	public void setUser_token(String user_token) {
		this.user_token = user_token;
	}

	public ScoredChange getsChange() {
		return sChange;
	}

	public void setsChange(ScoredChange sChange) {
		this.sChange = sChange;
	}
}
