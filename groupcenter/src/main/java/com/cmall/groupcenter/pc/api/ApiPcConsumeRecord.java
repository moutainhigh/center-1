package com.cmall.groupcenter.pc.api;

import com.cmall.groupcenter.pc.model.PcConsumeRecordInput;
import com.cmall.groupcenter.pc.model.PcConsumeRecordResult;
import com.cmall.groupcenter.service.GroupPcService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 
 * 微公社PC版本-消费明细
 * @author GaoYang
 * @CreateDate 2015年7月28日下午2:46:49
 *
 */
public class ApiPcConsumeRecord  extends RootApiForToken<PcConsumeRecordResult,PcConsumeRecordInput>{

	@Override
	public PcConsumeRecordResult Process(PcConsumeRecordInput inputParam,
			MDataMap mRequestMap) {
		
		PcConsumeRecordResult consumeResult = new PcConsumeRecordResult();
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		
		GroupPcService pcService = new GroupPcService();
		consumeResult = pcService.showPcConsumeRecord(accountCode,inputParam);
		return consumeResult;
	}

}
