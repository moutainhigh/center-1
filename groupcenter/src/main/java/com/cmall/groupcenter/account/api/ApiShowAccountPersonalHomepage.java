package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.AccountPersonalHomepageInput;
import com.cmall.groupcenter.account.model.AccountPersonalHomepageResult;
import com.cmall.groupcenter.service.GroupNewService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 
 * 获取个人主页信息
 * @author GaoYang
 * @CreateDate 2015年6月4日下午6:54:11
 *
 */
public class ApiShowAccountPersonalHomepage extends RootApiForToken<AccountPersonalHomepageResult,AccountPersonalHomepageInput>{

	public AccountPersonalHomepageResult Process(
			AccountPersonalHomepageInput inputParam, MDataMap mRequestMap) {
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
        GroupNewService groupService=new GroupNewService();
		return groupService.ShowAccountPersonalHomepage(accountCode, inputParam);
		
	}

}
