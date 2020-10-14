package com.cmall.groupcenter.pc.api;

import com.cmall.groupcenter.pc.model.PcAccountWithdrawRecordInput;
import com.cmall.groupcenter.pc.model.PcAccountWithdrawRecordResult;
import com.cmall.groupcenter.service.GroupPcService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 
 * 微公社PC版本-提现记录
 * @author GaoYang
 * @CreateDate 2015年9月8日上午11:43:42
 *
 */
public class ApiPcAccountWithdrawRecord extends RootApiForToken<PcAccountWithdrawRecordResult,PcAccountWithdrawRecordInput>{

	@Override
	public PcAccountWithdrawRecordResult Process(
			PcAccountWithdrawRecordInput inputParam, MDataMap mRequestMap) {
		
		PcAccountWithdrawRecordResult withdrawRecordResult = new PcAccountWithdrawRecordResult();
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		
		GroupPcService pcService = new GroupPcService();
		withdrawRecordResult = pcService.ShowPcWithdrawRecord(accountCode,inputParam);
		return withdrawRecordResult;
	}

}
