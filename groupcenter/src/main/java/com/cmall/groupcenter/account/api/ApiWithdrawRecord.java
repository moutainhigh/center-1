package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.WithdrawInfoResult;
import com.cmall.groupcenter.account.model.WithdrawRecordInput;
import com.cmall.groupcenter.account.model.WithdrawRecordResult;
import com.cmall.groupcenter.service.GroupService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 提款记录
 * @author chenbin
 *
 */
public class ApiWithdrawRecord extends RootApiForToken<WithdrawRecordResult,WithdrawRecordInput>{

	public WithdrawRecordResult Process(WithdrawRecordInput inputParam,
			MDataMap mRequestMap) {
		WithdrawRecordResult withdrawRecordResult=new WithdrawRecordResult();
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		
		GroupService groupService=new GroupService();
		withdrawRecordResult=groupService.shoWithdrawRecord(accountCode,inputParam);
		return withdrawRecordResult;
	}

}
