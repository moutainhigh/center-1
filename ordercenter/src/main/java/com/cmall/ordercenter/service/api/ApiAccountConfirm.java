package com.cmall.ordercenter.service.api;

import java.util.List;

import com.cmall.ordercenter.model.api.AccountConfirmResult;
import com.cmall.ordercenter.model.api.AccountDetailInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 *确认结算单 
 * 
 */
public class ApiAccountConfirm extends RootApi<AccountConfirmResult, AccountDetailInput> {

	public AccountConfirmResult Process(AccountDetailInput inputParam,
			MDataMap mRequestMap) {
		AccountConfirmResult result = new AccountConfirmResult();
		if(inputParam.getSeller_code()==null||"".equals(inputParam.getSeller_code())){
			result.setResultCode(939301013);
			result.setResultMessage(bInfo(939301013, "店铺编号"));
		}else if(inputParam.getStart_time()==null||"".equals(inputParam.getStart_time())){
			result.setResultCode(939301013);
			result.setResultMessage(bInfo(939301013, "起始时间"));
		}else if (inputParam.getEnd_time()==null||"".equals(inputParam.getEnd_time())) {
			result.setResultCode(939301013);
			result.setResultMessage(bInfo(939301013, "结束时间"));
		}else {
			MDataMap map = new MDataMap();
			map.put("start_time", inputParam.getStart_time());
			map.put("end_time", inputParam.getEnd_time());
			map.put("seller_code", inputParam.getSeller_code());
			List<MDataMap> list = DbUp.upTable("oc_accountinfo").queryAll("*", "", "", map);
			if(list.isEmpty()){
				result.setResultCode(939301080);
				result.setResultMessage(bInfo(939301080));
			}else{
				MDataMap updateMap = new MDataMap();
				updateMap.put("account_status", "4497153900030003");
				updateMap.put("zid", list.get(0).get("zid"));
				updateMap.put("uid", list.get(0).get("uid"));
				DbUp.upTable("oc_accountinfo").update(updateMap);
				result.setResultCode(939301081);
				result.setResultMessage(bInfo(939301081));
			}
		}
		return result;
	}

}
