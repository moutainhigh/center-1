package com.cmall.groupcenter.third.api;


import com.cmall.groupcenter.third.model.GetTraderInfoInput;
import com.cmall.groupcenter.third.model.GetTraderInfoResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 
 * 微公社商户后台-获取商户信息
 * @author huangs
 * 
 *
 */
public class ApiGetTraderInfoByTradercode extends RootApi<GetTraderInfoResult,GetTraderInfoInput> {

	public GetTraderInfoResult Process(GetTraderInfoInput inputParam, MDataMap mRequestMap) {
		

		return getTraderInfoByTradercode(inputParam);
	}


	public GetTraderInfoResult getTraderInfoByTradercode(GetTraderInfoInput inputParam) {
		MDataMap mTraderMap = DbUp.upTable("gc_trader_info").one("trader_code",
				inputParam.getTrader_code());
		GetTraderInfoResult result = new GetTraderInfoResult();
		if (result.getResultCode() == 1) {
			if (mTraderMap != null) {
				String gurrantee_balance = mTraderMap.get("gurrantee_balance");
				String trader_status = mTraderMap.get("trader_status");
				String create_time = mTraderMap.get("create_time");
				String last_login_date = mTraderMap.get("last_login_date");
				String uid = mTraderMap.get("uid");
				String trader_name = mTraderMap.get("trader_name");
				String trader_pic_url = mTraderMap.get("trader_pic_url");
				String activate_operation = mTraderMap.get("activate_operation");


				result.setGurrantee_balance(gurrantee_balance);
				result.setTrader_status(trader_status);
				result.setCreate_time(create_time);
				result.setLast_login_date(last_login_date);
				result.setUid(uid);
				result.setTrader_pic_url(trader_pic_url);
				result.setTrader_name(trader_name);
                result.setActivate_operation(activate_operation);
			}
		}
		return result;
	}

}
