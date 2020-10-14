package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class UserRegHomePoolResult extends HomePoolMemberResult {

	@ZapcomApi(value = "用户认证串", remark = "注册成功后返回非空，同登陆成功的token")
	private String user_token = "";

	public String getUser_token() {
		return user_token;
	}

	public void setUser_token(String user_token) {
		this.user_token = user_token;
	}

}
