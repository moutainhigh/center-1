package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.AccountPushSetInfoResult;
import com.cmall.groupcenter.account.model.AccountPushSetInput;
import com.cmall.groupcenter.service.GroupService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 修改账户推送设定信息的开关状态
 * @author GaoYang
 * @CreateDate 2015年5月20日下午8:55:26
 *
 */
public class ApiModifyAccountPushTypeOnoff extends RootApiForToken<AccountPushSetInfoResult,AccountPushSetInput>{

	public AccountPushSetInfoResult Process(AccountPushSetInput inputParam,
			MDataMap mRequestMap) {
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
        GroupService groupService=new GroupService();
		return groupService.ModifyAccountPushTypeOnoff(accountCode, inputParam);
		
	}

}
