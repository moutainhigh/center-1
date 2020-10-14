package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 用户 - 修改密码
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class UserChangePasswordInput extends RootInput{
	
	@ZapcomApi(value="旧密码",demo="123456")
	private String old_password="";

	@ZapcomApi(value="新密码",demo="654321")
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
