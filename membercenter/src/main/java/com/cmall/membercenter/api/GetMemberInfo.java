package com.cmall.membercenter.api;

import com.cmall.membercenter.model.GetMemberResult;
import com.cmall.membercenter.model.MemberResult;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 
 * 获取用户信息
 * 
 * @author srnpr
 * 
 */
public class GetMemberInfo extends RootApiForToken<GetMemberResult, RootInput> {

	public GetMemberResult Process(RootInput inputParam, MDataMap mRequestMap) {
		// TODO Auto-generated method stub
		// return null;

		MemberLoginSupport memberLoginSupport = new MemberLoginSupport();

		GetMemberResult getInfoResult = memberLoginSupport
				.getMemberInfo(getUserCode());

		return getInfoResult;
	}

}
