package com.cmall.groupcenter.pc.api;

import com.cmall.groupcenter.pc.model.PcRebateRecordInput;
import com.cmall.groupcenter.pc.model.PcRebateRecordResult;
import com.cmall.groupcenter.service.GroupPcService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 
 * 微公社PC版本-返利明细
 * @author GaoYang
 * @CreateDate 2015年7月22日上午10:23:22
 *
 */
public class ApiPcRebateRecord extends RootApiForToken<PcRebateRecordResult,PcRebateRecordInput>{

	@Override
	public PcRebateRecordResult Process(PcRebateRecordInput inputParam,
			MDataMap mRequestMap) {
		PcRebateRecordResult rebateResult = new PcRebateRecordResult();
		
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		
		GroupPcService pcService = new GroupPcService();
		rebateResult = pcService.ShowPcRebateRecord(accountCode,inputParam);
		
		return rebateResult;
	}

}
