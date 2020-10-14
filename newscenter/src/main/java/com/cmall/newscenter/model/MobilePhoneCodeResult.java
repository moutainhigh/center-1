package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 用户 - 获取手机验证码输出类
 * @author shiyz
 * date 2014-7-18
 * @version 1.0
 */
public class MobilePhoneCodeResult extends RootResultWeb {
	
	@ZapcomApi(value = "手机验证码")
	String yzm = "";

	public String getYzm() {
		return yzm;
	}

	public void setYzm(String yzm) {
		this.yzm = yzm;
	}

}
