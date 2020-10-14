package com.cmall.ordercenter.service.api;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import com.cmall.ordercenter.model.api.AccountDetailInput;
import com.cmall.ordercenter.model.api.AccountResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 *根据店铺编号和起止时间获取结算明细 
 * 
 */
public class ApiGetAccountDetails extends RootApi<AccountResult, AccountDetailInput> {

	public AccountResult Process(AccountDetailInput inputParam,
			MDataMap mRequestMap) {
		AccountResult result = new AccountResult();
		if(inputParam.getSeller_code()==null||"".equals(inputParam.getSeller_code())){
			result.setResultCode(939301013);
			result.setResultMessage(bInfo(939301013, "店铺编号"));
		}else if(inputParam.getStart_time()==null||"".equals(inputParam.getStart_time())){
			result.setResultCode(939301013);
			result.setResultMessage(bInfo(939301013, "起始时间"));
		}else if (inputParam.getEnd_time()==null||"".equals(inputParam.getEnd_time())) {
			result.setResultCode(939301013);
			result.setResultMessage(bInfo(939301013, "结束时间"));
		}
		MDataMap map = new MDataMap();
		map.put("start_time", inputParam.getStart_time());
		map.put("end_time", inputParam.getEnd_time());
		map.put("seller_code", inputParam.getSeller_code());
		List<MDataMap> accoutCodes = DbUp.upTable("oc_accountinfo").queryAll("account_code", "", "", map);
		if(!accoutCodes.isEmpty()){
			List<String> lStrings=new ArrayList<String>();
			for (MDataMap mDataMap : accoutCodes) {
				lStrings.add(mDataMap.get("account_code"));
			}
			List<MDataMap> list = DbUp.upTable("oc_accountinfo_relation").queryIn("", "", "", new MDataMap(), 0, 0, "account_code", StringUtils.join(lStrings,","));
			result.setList(list);
		}
		return result;
	}

}
