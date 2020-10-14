package com.cmall.ordercenter.service.api;

import java.util.List;

import com.cmall.ordercenter.model.api.AccountInput;
import com.cmall.ordercenter.model.api.AccountResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
/**
 *根据店铺编号获取店铺所有结算单 
 * 
 */

public class ApiGetAccounts extends RootApi<AccountResult, AccountInput> {

	public AccountResult Process(AccountInput inputParam, MDataMap mRequestMap) {
		AccountResult result = new AccountResult();
		if(inputParam.getSeller_code()==null||"".equals(inputParam.getSeller_code())){//参数不能为空或者null
			result.setResultCode(939301013);
			result.setResultMessage(bInfo(939301013, "店铺编号"));
		}else{
			MDataMap map = new MDataMap();
			map.put("seller_code", inputParam.getSeller_code());
			List<MDataMap> list = DbUp.upTable("oc_accountinfo").queryAll("", "", "", map);
			result.setList(list);
		}
		return result;
	}

}
