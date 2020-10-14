package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.AccountFriendsListInput;
import com.cmall.groupcenter.account.model.AccountFriendsListResult;
import com.cmall.groupcenter.service.GroupNewService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 获取好友列表信息
 * @author GaoYang
 * @CreateDate 2015年6月2日下午6:44:00
 *
 */
public class ApiShowAccountFriendsList  extends RootApiForToken<AccountFriendsListResult,AccountFriendsListInput>{

	public AccountFriendsListResult Process(AccountFriendsListInput inputParam,
			MDataMap mRequestMap) {
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
        GroupNewService groupService=new GroupNewService();
		return groupService.ShowAccountFriendsList(accountCode, inputParam);
	}

}
