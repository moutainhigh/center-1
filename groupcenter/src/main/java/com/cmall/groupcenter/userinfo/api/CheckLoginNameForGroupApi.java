package com.cmall.groupcenter.userinfo.api;


import com.cmall.membercenter.model.CheckLoginNameForGroupInput;
import com.cmall.membercenter.support.MemberInfoSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 微工社注册验证用户名是否存在
 * 
 * @author chenxk
 *
 */
public class CheckLoginNameForGroupApi extends
	RootApiForManage<RootResultWeb, CheckLoginNameForGroupInput> {

	public RootResultWeb Process(
			CheckLoginNameForGroupInput inputParam, MDataMap mRequestMap) {
		
		return new MemberInfoSupport().checkLoginNameIsExist(inputParam.getLoginName());
	}
}
