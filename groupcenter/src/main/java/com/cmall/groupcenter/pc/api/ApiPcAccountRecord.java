package com.cmall.groupcenter.pc.api;

import com.cmall.groupcenter.pc.model.PcAccountRecordInput;
import com.cmall.groupcenter.pc.model.PcAccountRecordResult;
import com.cmall.groupcenter.service.GroupPcService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 
 * 微公社PC版本-账户明细
 * @author GaoYang
 * @CreateDate 2015年7月27日上午11:44:13
 *
 */
public class ApiPcAccountRecord  extends RootApiForToken<PcAccountRecordResult,PcAccountRecordInput>{

	@Override
	public PcAccountRecordResult Process(PcAccountRecordInput inputParam,
			MDataMap mRequestMap) {
		PcAccountRecordResult accountResult = new PcAccountRecordResult();
		
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		
		GroupPcService pcService = new GroupPcService();
		accountResult = pcService.showPcAccountRecord(accountCode,inputParam);
		return accountResult;
	}

}
