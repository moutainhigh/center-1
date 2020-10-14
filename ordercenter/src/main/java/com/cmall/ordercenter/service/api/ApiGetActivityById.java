package com.cmall.ordercenter.service.api;

import com.cmall.ordercenter.model.api.ApiGetActivityByIdResult;
import com.cmall.ordercenter.model.api.ApiGetActivitysByIdInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

public class ApiGetActivityById extends RootApi<ApiGetActivityByIdResult,ApiGetActivitysByIdInput> {

	public ApiGetActivityByIdResult Process(ApiGetActivitysByIdInput api, MDataMap mRequestMap) {
		
		ApiGetActivityByIdResult result = new ApiGetActivityByIdResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			MDataMap acMap = DbUp.upTable("oc_activity").oneWhere("activity_code,activity_name,remark,begin_time,end_time", "", "", "activity_code",api.getActivityCode());
			if(acMap == null){
				acMap = new MDataMap();
			}
			result.setAcMap(acMap);
		}
		return result;
	}
}
