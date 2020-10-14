package com.cmall.ordercenter.service.api;

import java.util.List;

import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.api.ApiGetOrdersInput;
import com.cmall.ordercenter.model.api.ApiGetOrdersResult;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiGetOrders extends RootApi<ApiGetOrdersResult,ApiGetOrdersInput> {

	public ApiGetOrdersResult Process(ApiGetOrdersInput api, MDataMap mRequestMap) {
		ApiGetOrdersResult result = new ApiGetOrdersResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			OrderService os = new OrderService();
			
			try {
				
				List<Order> list = os.getOrderList(api.getBuyerCode(),api.getFromTime(),api.getOrderStatus(),api.getOrderType(),api.getOrderChannel());
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