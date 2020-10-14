package com.cmall.groupcenter.pc.api;

import com.cmall.groupcenter.pc.model.PcCutPaymentRecordInput;
import com.cmall.groupcenter.pc.model.PcCutPaymentRecordResult;
import com.cmall.groupcenter.service.GroupPcService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 
 * 微公社PC版本-扣款明细
 * @author GaoYang
 * @CreateDate 2015年7月23日下午6:07:26
 *
 */
public class ApiPcCutPaymentRecord  extends RootApiForToken<PcCutPaymentRecordResult,PcCutPaymentRecordInput>{

	@Override
	public PcCutPaymentRecordResult Process(PcCutPaymentRecordInput inputParam,
			MDataMap mRequestMap) {
		
		PcCutPaymentRecordResult cutPaymentResult = new PcCutPaymentRecordResult();
		
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		
		GroupPcService pcService = new GroupPcService();
		cutPaymentResult = pcService.ShowPcCutPaymentRecord(accountCode,inputParam);
		
		return cutPaymentResult;
	}

}
