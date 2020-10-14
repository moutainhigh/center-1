package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class AccountInfoByMobileInput extends RootInput  {

	@ZapcomApi(value="手机号",require=1)
	private String mobile="";

	/**
	 * 获取  mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * 设置 
	 * @param mobile 
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
}
