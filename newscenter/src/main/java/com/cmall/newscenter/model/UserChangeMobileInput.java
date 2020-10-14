package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 用户 - 修改手机号
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class UserChangeMobileInput extends RootInput{
	
	@ZapcomApi(value="手机号",demo="13520351350")
	private String mobile="";

	@ZapcomApi(value="验证码",demo="abcd")
	private String code="";

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
