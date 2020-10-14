package com.cmall.groupcenter.groupapp.api;

import com.cmall.groupcenter.groupapp.model.GetFriendsListInput;
import com.cmall.groupcenter.groupapp.model.GetFriendsListResult;
import com.cmall.groupcenter.groupapp.service.GetFriendsListService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 获取通讯录
 * @author GaoYang
 * @CreateDate 2015年11月10日下午3:27:23
 *
 */
public class ApiGetFriendsListInfo extends RootApiForToken<GetFriendsListResult,GetFriendsListInput>{

	@Override
	public GetFriendsListResult Process(GetFriendsListInput inputParam,
			MDataMap mRequestMap) {
		
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		GetFriendsListService fListervice=new GetFriendsListService();
		return fListervice.ShowAccountFriendsList(accountCode, inputParam);
	}

}
