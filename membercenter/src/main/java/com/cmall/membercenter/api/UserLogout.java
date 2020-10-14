package com.cmall.membercenter.api;

import com.cmall.membercenter.model.UserLogoutInput;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForMember;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 用户注销接口
 * 
 * @author srnpr
 * 
 */
public class UserLogout extends RootApiForMember<RootResultWeb, UserLogoutInput> {

	public MWebResult Process(UserLogoutInput inputParam, MDataMap mRequestMap) {

		
		if(getFlagLogin())
		{
			String sAccessToken = getOauthInfo().getAccessToken();

			return new MemberLoginSupport().memberLogout(sAccessToken,inputParam.getSerialNumber());

		}
		else
		{
			return new MWebResult();
		}
		
		
	}

}
