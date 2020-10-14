package com.cmall.membercenter.api;

import com.cmall.membercenter.model.UserTokenInput;
import com.cmall.membercenter.model.UserTokenResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForToken;

public class UserToken extends RootApiForToken<UserTokenResult, UserTokenInput> {

	public UserTokenResult Process(UserTokenInput inputParam,
			MDataMap mRequestMap) {
		UserTokenResult userTokenResult = new UserTokenResult();

		userTokenResult.setMemberCode(getUserCode());
		return userTokenResult;
	}

}
