package com.cmall.ordercenter.service.api;

import com.cmall.ordercenter.model.GetOrderFreightInput;
import com.cmall.ordercenter.model.GetOrderFreightResult;
import com.cmall.ordercenter.service.GetOrderFreightService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

/**
 * 
 * 返回订单运费
 * @author huoqiangshou
 *
 */
public class ApiGetOrderFreight extends RootApi<GetOrderFreightResult, GetOrderFreightInput> {

	public GetOrderFreightResult Process(GetOrderFreightInput inputParam,
			MDataMap mRequestMap) {
		GetOrderFreightService service = new GetOrderFreightService();
		
		GetOrderFreightResult result = service.getOrderFreight(inputParam);
		// TODO Auto-generated method stub
		return result;
	}

}
