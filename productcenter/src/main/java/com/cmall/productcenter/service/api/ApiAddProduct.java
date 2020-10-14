package com.cmall.productcenter.service.api;

import java.util.List;


import com.cmall.productcenter.model.Category;
import com.cmall.productcenter.model.PcProductInfoForI;
import com.cmall.productcenter.model.api.ApiAddProductInput;
import com.cmall.productcenter.model.api.ApiAddProductResult;
import com.cmall.productcenter.service.CategoryService;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiAddProduct extends RootApi<ApiAddProductResult,ApiAddProductInput> {

	public ApiAddProductResult Process(ApiAddProductInput api, MDataMap mRequestMap) {
		ApiAddProductResult result = new ApiAddProductResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			ProductService s = new ProductService();
			StringBuffer error = new StringBuffer();
			int retCode = s.AddProduct(api.getProduct(), error);
			
			result.setResultCode(retCode);
			result.setResultMessage(error.toString());
		}
		
		return result;
	}
}
