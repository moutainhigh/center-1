package com.cmall.membercenter.group.api;

import com.cmall.membercenter.group.model.GroupLoginInput;
import com.cmall.membercenter.group.model.GroupLoginResult;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 微公社账户登陆
 * 
 * @author srnpr
 *
 */
public class ApiGroupLogin extends
		RootApiForManage<GroupLoginResult, GroupLoginInput> {

	public GroupLoginResult Process(GroupLoginInput inputParam,
			MDataMap mRequestMap) {

		return new MemberLoginSupport().doGroupLogin(inputParam,
				getManageCode());
	}

}
