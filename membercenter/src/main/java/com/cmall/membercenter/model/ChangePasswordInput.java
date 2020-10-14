package com.cmall.membercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ChangePasswordInput extends RootInput {

	
	@ZapcomApi(value = "旧密码", require = 1, demo = "123456", remark = "旧密码，长度为6-40位，支持特殊字符。", verify = {
			"minlength=6", "maxlength=40" })
	private String old_password="";
	
	@ZapcomApi(value = "新密码", require = 1, demo = "123456", remark = "新密码，长度为6-40位，支持特殊字符。", verify = {
			"minlength=6", "maxlength=40" })
	private String new_password="";

	public String getOld_password() {
		return old_password;
	}

	public void setOld_password(String old_password) {
		this.old_password = old_password;
	}

	public String getNew_password() {
		return new_password;
	}

	public void setNew_password(String new_password) {
		this.new_password = new_password;
	}
	
	
	
}
