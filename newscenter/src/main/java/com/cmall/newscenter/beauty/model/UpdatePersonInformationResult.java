package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;


/**
 * 惠美丽-修改个人资料输出类
 * @author yangrong	
 * date 2014-8-20
 * @version 1.0
 */
public class UpdatePersonInformationResult extends RootResultWeb {

	@ZapcomApi(value = "用户信息")
	private Userinfo userInfo = new Userinfo();

	public Userinfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(Userinfo userInfo) {
		this.userInfo = userInfo;
	}
	
	
}
