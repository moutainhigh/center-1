package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.RebateRecordDetailInput;
import com.cmall.groupcenter.account.model.RebateRecordDetailResult;
import com.cmall.groupcenter.service.GroupService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 
 * 新版本返利明细详情(2.1.4版)
 * @author GaoYang
 * @CreateDate 2015年5月21日下午2:20:38
 *
 */
public class ApiRebateRecordDetail extends RootApiForToken<RebateRecordDetailResult,RebateRecordDetailInput>{

	public RebateRecordDetailResult Process(RebateRecordDetailInput inputParam,
			MDataMap mRequestMap) {
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
        GroupService groupService=new GroupService();
		return groupService.ShowRebateRecordDetail(accountCode, inputParam);
		
	}

}
