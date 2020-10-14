package com.cmall.membercenter.oauth.api;


import com.cmall.membercenter.oauth.model.CheckUserInfoInput;
import com.cmall.membercenter.oauth.model.CheckUserInfoResult;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

@ZapcomApi(value = "用户获取登陆，使用用户名/密码")
public class CheckUserInfo extends
		RootApiForManage<CheckUserInfoResult, CheckUserInfoInput> {

	public CheckUserInfoResult Process(CheckUserInfoInput inputParam,
			MDataMap mRequestMap) {
		
		return new MemberLoginSupport().doUserLoginByThirdParty(inputParam, getManageCode());
	}

}
