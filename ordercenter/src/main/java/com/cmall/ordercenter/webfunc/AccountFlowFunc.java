package com.cmall.ordercenter.webfunc;

import com.cmall.ordercenter.service.AccountInfoService;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 结算信息状态审批时记录日志
 * 
 * @author jack
 * @version 1.0
 * */
public class AccountFlowFunc implements IFlowFunc {

	public RootResult BeforeFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus,MDataMap mSubMap) {
		return null;
	}

	public RootResult afterFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus,MDataMap mSubMap) {
		RootResult result = new RootResult();
		MDataMap map = DbUp.upTable("oc_accountinfo").one("uid", outCode);
		String accountCode = map.get("account_code");
		if (accountCode != null && !"".equals(accountCode)) {
			map.put("account_status", toStatus);
			map.put("account_code", accountCode);
			AccountInfoService service = new AccountInfoService();
			service.insertAccountStatusLog(map);
		}
		// 对日志的处理
		result.setResultCode(1);
		return result;
	}

}
