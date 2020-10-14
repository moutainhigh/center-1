package com.cmall.usercenter.service.api;

import java.util.List;

import com.cmall.usercenter.model.CollectionSellerModel;
import com.cmall.usercenter.model.UcSellerInfo;
import com.cmall.usercenter.model.api.ApiGetSellerInput;
import com.cmall.usercenter.model.api.ApiGetSellerResult;
import com.cmall.usercenter.service.SellerInfoService;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetSellers extends RootApi<ApiGetSellerResult,ApiGetSellerInput> {

	public ApiGetSellerResult Process(ApiGetSellerInput api, MDataMap mRequestMap) {
		ApiGetSellerResult result = new ApiGetSellerResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			SellerInfoService sis  = new SellerInfoService();
			
			try {
				
				List<UcSellerInfo> list = sis.getSellerInfo(api.getSellerCodes());
				
				result.setResultCode(1);
				result.setSellerInfoList(list);
				
			} catch (Exception e) {
				result.setResultCode(939301033);
				result.setResultMessage(bInfo(939301033));
			}
			
		}
		
		return result;
	}
}