package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.RebateRecordNewVersionResult;
import com.cmall.groupcenter.account.model.WithdrawRecordNewVersionInput;
import com.cmall.groupcenter.service.GroupService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 新版本返利明细(2.1.4版)
 * @author GaoYang
 * @CreateDate 2015年5月19日上午11:31:59
 */
public class ApiRebateRecordForNewVersion extends RootApiForToken<RebateRecordNewVersionResult,WithdrawRecordNewVersionInput>{

	public RebateRecordNewVersionResult Process(WithdrawRecordNewVersionInput inputParam,
			MDataMap mRequestMap) {
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
        GroupService groupService=new GroupService();
		return groupService.showRebateRecordForNewVersion(accountCode, inputParam);
	}

}
