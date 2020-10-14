package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.AccountPushSetInfoResult;
import com.cmall.groupcenter.account.model.AccountPushSetInput;
import com.cmall.groupcenter.service.GroupService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 账户推送设定信息
 * @author GaoYang
 * @CreateDate 2015年5月20日下午6:08:32
 */
public class ApiShowAccountPushSetInfo extends RootApiForToken<AccountPushSetInfoResult,RootInput>{

	public AccountPushSetInfoResult Process(RootInput inputParam,
			MDataMap mRequestMap) {
		
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
        GroupService groupService=new GroupService();
		return groupService.ShowAccountPushSetInfo(accountCode, inputParam);
		
	}

}
