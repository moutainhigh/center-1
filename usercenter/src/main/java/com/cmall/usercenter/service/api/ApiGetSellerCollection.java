package com.cmall.usercenter.service.api;

import java.util.List;

import com.cmall.usercenter.model.CollectionSellerModel;
import com.cmall.usercenter.model.api.ApiGetSellerCollectionInput;
import com.cmall.usercenter.model.api.ApiGetSellerCollectionResult;
import com.cmall.usercenter.service.SellerInfoService;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetSellerCollection extends RootApi<ApiGetSellerCollectionResult,ApiGetSellerCollectionInput> {

	public ApiGetSellerCollectionResult Process(ApiGetSellerCollectionInput api, MDataMap mRequestMap) {
		ApiGetSellerCollectionResult result = new ApiGetSellerCollectionResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			SellerInfoService sis  = new SellerInfoService();
			
			try {
				
				List<CollectionSellerModel> list = sis.getCollectionSellerInfos(api.getSellerCodes());
				
				result.setResultCode(1);
				result.setCollectionList(list);
				
			} catch (Exception e) {
				result.setResultCode(939301033);
				result.setResultMessage(bInfo(939301033));
			}
			
		}
		
		return result;
	}
}