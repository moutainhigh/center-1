package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ChangePasswordForWxInput extends RootInput {

	
	@ZapcomApi(value = "新密码", require = 1, demo = "123456", remark = "新密码，支持特殊字符。")
	private String new_password="";

	public String getNew_password() {
		return new_password;
	}

	public void setNew_password(String new_password) {
		this.new_password = new_password;
	}
}
