package com.cmall.ordercenter.service.api;

import java.util.List;

import com.cmall.ordercenter.model.OcActivity;
import com.cmall.ordercenter.model.api.ApiConfirmReceiveInput;
import com.cmall.ordercenter.model.api.ApiConfirmReceiveResult;
import com.cmall.ordercenter.service.ActivityService;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webfactory.UserFactory;

public class ApiConfirmReceive extends RootApi<ApiConfirmReceiveResult,ApiConfirmReceiveInput> {

	public ApiConfirmReceiveResult Process(ApiConfirmReceiveInput api, MDataMap mRequestMap) {
		ApiConfirmReceiveResult result = new ApiConfirmReceiveResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			OrderService os = new OrderService();
			
			try {
				
				RootResult rr = os.changForRecieveByUser(api.getOrderCode(),"");
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