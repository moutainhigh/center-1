package com.cmall.ordercenter.service.api;

import java.util.List;

import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.api.ApiPaySuccessInput;
import com.cmall.ordercenter.model.api.ApiPaySuccessResult;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiPaySuccess extends RootApi<ApiPaySuccessResult,ApiPaySuccessInput> {

	public ApiPaySuccessResult Process(ApiPaySuccessInput api, MDataMap mRequestMap) {
		ApiPaySuccessResult result = new ApiPaySuccessResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			OrderService os = new OrderService();
			
			try {
				RootResult rr = os.paySucess(api.getPay());
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