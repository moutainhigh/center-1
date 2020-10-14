package com.cmall.productcenter.service.api;

import java.util.List;


import com.cmall.productcenter.model.api.ApiChangeProductSkuInput;
import com.cmall.productcenter.model.api.ApiChangeProductSkuResult;

import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiChangeProductSku extends RootApi<ApiChangeProductSkuResult,ApiChangeProductSkuInput> {

	public ApiChangeProductSkuResult Process(ApiChangeProductSkuInput api, MDataMap mRequestMap) {
		ApiChangeProductSkuResult result = new ApiChangeProductSkuResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			ProductService s = new ProductService();
			StringBuffer error = new StringBuffer();
			//int retCode = s.AddProduct(api.getProduct(), error);
			
			result.setResultCode(1);
			result.setResultMessage(error.toString());
		}
		
		return result;
	}
}
