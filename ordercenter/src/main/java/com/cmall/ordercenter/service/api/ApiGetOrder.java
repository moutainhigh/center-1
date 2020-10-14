package com.cmall.ordercenter.service.api;

import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.api.ApiConfirmReceiveInput;
import com.cmall.ordercenter.model.api.ApiGetOrderInput;
import com.cmall.ordercenter.model.api.ApiGetOrderResult;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetOrder extends RootApi<ApiGetOrderResult, ApiGetOrderInput> {

	public ApiGetOrderResult Process(ApiGetOrderInput api, MDataMap mRequestMap) {
		ApiGetOrderResult result = new ApiGetOrderResult();
		if (api == null) {
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		} else {

			OrderService os = new OrderService();

			try {

				Order order = os.getOrderByBuyer(api.getOrderCode(), api.getBuyerCode());

				if (order != null) {
					result.setOrder(order);
					result.setResultCode(1);
				}
				else{
					
					result.setResultCode(939301031);
					result.setResultMessage(bInfo(939301031));
				}
			} catch (Exception e) {
				result.setResultCode(939301033);
				result.setResultMessage(bInfo(939301033));
			}

		}

		return result;
	}
}