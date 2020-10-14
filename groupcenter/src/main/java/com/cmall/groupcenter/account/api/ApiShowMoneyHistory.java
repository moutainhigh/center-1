package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.ShowMoneyHistoryResult;
import com.cmall.groupcenter.service.GroupService;
import com.srnpr.zapcom.baseface.IBaseInput;
import com.srnpr.zapcom.baseface.IBaseResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

public class ApiShowMoneyHistory extends RootApiForToken<ShowMoneyHistoryResult, RootInput>{

	public ShowMoneyHistoryResult Process(RootInput inputParam,
			MDataMap mRequestMap) {
		ShowMoneyHistoryResult showMoneyHistoryResult=new ShowMoneyHistoryResult();
		GroupService groupService=new GroupService();
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		showMoneyHistoryResult=groupService.ShowMoneyHistory(accountCode);
		return showMoneyHistoryResult;
	}



}
