package com.cmall.ordercenter.service.api;

import java.util.List;

import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderStatusGroupModel;
import com.cmall.ordercenter.model.api.ApiGetOrdersInput;
import com.cmall.ordercenter.model.api.ApiGetOrdersStatusCountResult;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiGetOrdersStatusCount extends RootApi<ApiGetOrdersStatusCountResult,ApiGetOrdersInput> {

	public ApiGetOrdersStatusCountResult Process(ApiGetOrdersInput api, MDataMap mRequestMap) {
		ApiGetOrdersStatusCountResult result = new ApiGetOrdersStatusCountResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			OrderService os = new OrderService();
			
			try {
				
				OrderStatusGroupModel ret = os.getOrderStatusGroupCount(api.getBuyerCode(),api.getFromTime(),api.getOrderStatus(),api.getOrderType(),api.getOrderChannel());
				result.setOsgm(ret);
				result.setResultCode(1);
				
			} catch (Exception e) {
				result.setResultCode(939301033);
				result.setResultMessage(bInfo(939301033));
			}
			
		}
		
		return result;
	}
}