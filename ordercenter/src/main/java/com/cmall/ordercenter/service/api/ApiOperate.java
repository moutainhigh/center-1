package com.cmall.ordercenter.service.api;

import java.util.List;

import com.cmall.ordercenter.model.api.ApiOperateInput;
import com.cmall.ordercenter.model.api.ApiOperateResult;

import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiOperate extends RootApi<ApiOperateResult,ApiOperateInput> {

	public ApiOperateResult Process(ApiOperateInput api, MDataMap mRequestMap) {
		ApiOperateResult result = new ApiOperateResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			OrderService os = new OrderService();
			
			try {
				
				RootResult rr = os.operate(api,"");
				result.setResultCode(rr.getResultCode());
				result.setResultMessage(rr.getResultMessage());
				
			} catch (Exception e) {
				result.setResultCode(939301033);
				result.setResultMessage(bInfo(939301033));
			}
			
		}
		
		return result;
	}
}