package com.cmall.membercenter.agent;

import com.cmall.membercenter.model.AgentMemberResult;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class AgentLoginResult extends AgentMemberResult {

	@ZapcomApi(value = "用户认证串", remark = "登陆成功后返回非空，用于需要用户授权api_token的操作")
	private String user_token = "";
	
	public String getUser_token() {
		return user_token;
	}

	public void setUser_token(String user_token) {
		this.user_token = user_token;
	}


}
