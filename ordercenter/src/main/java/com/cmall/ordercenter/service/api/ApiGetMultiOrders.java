package com.cmall.ordercenter.service.api;

import java.util.List;

import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.api.ApiGetMultiOrdersInput;
import com.cmall.ordercenter.model.api.ApiGetOrdersResult;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiGetMultiOrders extends RootApi<ApiGetOrdersResult,ApiGetMultiOrdersInput> {

	public ApiGetOrdersResult Process(ApiGetMultiOrdersInput api, MDataMap mRequestMap) {
		ApiGetOrdersResult result = new ApiGetOrdersResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			OrderService os = new OrderService();
			
			try {
				
				List<Order> list = os.getMultiOrdersList(api.getBuyerCode(),api.getOrderCodes());
				result.setList(list);
				result.setResultCode(1);
				
			} catch (Exception e) {
				result.setResultCode(939301033);
				result.setResultMessage(bInfo(939301033));
			}
			
		}
		
		return result;
	}
}