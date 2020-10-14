package com.cmall.ordercenter.service.api;

import java.util.List;

import com.cmall.ordercenter.model.OcActivity;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.api.ApiOrderServiceInput;
import com.cmall.ordercenter.model.api.ApiOrderServiceResult;

import com.cmall.ordercenter.service.ActivityService;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webfactory.UserFactory;

public class ApiOrderService extends RootApi<ApiOrderServiceResult,ApiOrderServiceInput> {

	public ApiOrderServiceResult Process(ApiOrderServiceInput api, MDataMap mRequestMap) {
		ApiOrderServiceResult result = new ApiOrderServiceResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			OrderService s = new OrderService();
			StringBuffer error = new StringBuffer();
			List<Order> list = api.getOrderList();
			
			//增加校验
			if(list== null || list.size() == 0){
				result.setResultCode(939301088);
				result.setResultMessage(bInfo(939301088));
			}else{
				RootResult retrr = new RootResult();
				//是否频繁下单
				//retrr = s.FrequentlyToOrder(list.get(0).getBuyerCode());
				//if(retrr.getResultCode() == 1){
					//是否上架或者价格过低
					retrr = s.IsProductForSaleAndForLowPrice(list);
					if(retrr.getResultCode() == 1){
						
						int ret = s.AddOrderListTx(list, error);
						
						result.setResultCode(ret);
						result.setResultMessage(error.toString());
						
					}else{
						result.setResultCode(retrr.getResultCode());
						result.setResultMessage(retrr.getResultMessage());
					}
				//}else{
					//result.setResultCode(retrr.getResultCode());
					//result.setResultMessage(retrr.getResultMessage());
				//}
			}
		}
		
		return result;
	}
}
