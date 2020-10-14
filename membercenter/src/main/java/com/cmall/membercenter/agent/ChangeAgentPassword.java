package com.cmall.membercenter.agent;

import com.cmall.membercenter.model.ChangePasswordInput;
import com.cmall.membercenter.support.MemberInfoSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 修改密码
 * 
 * @author srnpr
 * 
 */
public class ChangeAgentPassword extends
		RootApiForToken<RootResultWeb, ChangePasswordInput> {

	public RootResultWeb Process(ChangePasswordInput inputParam,
			MDataMap mRequestMap) {

		MemberInfoSupport memberInfoSupport = new MemberInfoSupport();

		return memberInfoSupport.changeAgentPassword(getUserCode(), inputParam);
	}

}
