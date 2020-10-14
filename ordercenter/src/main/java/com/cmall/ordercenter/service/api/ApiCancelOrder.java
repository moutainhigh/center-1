package com.cmall.ordercenter.service.api;

import com.cmall.ordercenter.model.api.ApiCancelOrderInput;
import com.cmall.ordercenter.model.api.ApiCancelOrderResult;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiCancelOrder extends RootApi<ApiCancelOrderResult,ApiCancelOrderInput> {

	public ApiCancelOrderResult Process(ApiCancelOrderInput api, MDataMap mRequestMap) {
		ApiCancelOrderResult result = new ApiCancelOrderResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			OrderService os = new OrderService();
			
			try {
				result = os.CancelOrderForList(api.getOrderCodes());
			} catch (Exception e) {
				result.setResultCode(939301033);
				result.setResultMessage(bInfo(939301033));
			}
		}
		
		return result;
	}
}