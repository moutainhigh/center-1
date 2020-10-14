package com.cmall.groupcenter.pc.api;

import com.cmall.groupcenter.pc.model.PcAccountAssetsResult;
import com.cmall.groupcenter.service.GroupPcService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 
 * 微公社PC版本-用户资产接口
 * @author GaoYang
 * @CreateDate 2015年7月22日上午10:23:22
 *
 */
public class ApiPcAccountAssets extends RootApiForToken<PcAccountAssetsResult,RootInput>{

	@Override
	public PcAccountAssetsResult Process(RootInput inputParam,
			MDataMap mRequestMap) {
		PcAccountAssetsResult assetsResult = new PcAccountAssetsResult();
		
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		
		GroupPcService pcService = new GroupPcService();
		assetsResult = pcService.ShowPcAccountAssets(accountCode,inputParam);
		
		return assetsResult;
	}

}
