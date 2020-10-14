package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.AccountRecordInput;
import com.cmall.groupcenter.account.model.AccountRecordResult;
import com.cmall.groupcenter.service.GroupService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 
 * 账户明细信息
 * @author GaoYang
 * @CreateDate 2015年5月22日上午9:39:59
 * 
 */
public class ApiShowAccountRecord extends RootApiForToken<AccountRecordResult,AccountRecordInput>{

	public AccountRecordResult Process(AccountRecordInput inputParam,
			MDataMap mRequestMap) {
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
        GroupService groupService=new GroupService();
		return groupService.ShowAccountRecord(accountCode, inputParam);
		
	}

}
