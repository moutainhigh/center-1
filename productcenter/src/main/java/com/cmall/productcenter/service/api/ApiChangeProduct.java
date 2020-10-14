package com.cmall.productcenter.service.api;


import com.cmall.productcenter.model.api.ApiChangeProductInput;
import com.cmall.productcenter.model.api.ApiChangeProductResult;

import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiChangeProduct extends RootApi<ApiChangeProductResult,ApiChangeProductInput> {

	public ApiChangeProductResult Process(ApiChangeProductInput api, MDataMap mRequestMap) {
		ApiChangeProductResult result = new ApiChangeProductResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			ProductService s = new ProductService();
			StringBuffer error = new StringBuffer();
			int retCode = s.updateProduct(api.getProduct(), error);
			
			result.setResultCode(retCode);
			result.setResultMessage(error.toString());
		}
		
		return result;
	}
}
