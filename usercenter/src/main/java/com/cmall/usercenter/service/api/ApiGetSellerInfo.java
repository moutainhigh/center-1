package com.cmall.usercenter.service.api;

import java.util.List;

import com.cmall.usercenter.model.CollectionSellerModel;
import com.cmall.usercenter.model.UcSellerInfo;
import com.cmall.usercenter.model.api.ApiGetSellerInfoInput;
import com.cmall.usercenter.model.api.ApiGetSellerInfoResult;
import com.cmall.usercenter.service.SellerInfoService;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetSellerInfo extends RootApi<ApiGetSellerInfoResult,ApiGetSellerInfoInput> {

	public ApiGetSellerInfoResult Process(ApiGetSellerInfoInput api, MDataMap mRequestMap) {
		ApiGetSellerInfoResult result = new ApiGetSellerInfoResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			SellerInfoService sis  = new SellerInfoService();
			
			try {
				
				UcSellerInfo usi = sis.getSellerInfoBydomain(api.getSellerDomain());
				
				result.setResultCode(1);
				result.setSellerInfo(usi);
				
			} catch (Exception e) {
				result.setResultCode(939301033);
				result.setResultMessage(bInfo(939301033));
			}
			
		}
		
		return result;
	}
}