package com.cmall.productcenter.service.api;

import java.util.List;


import com.cmall.productcenter.model.Category;
import com.cmall.productcenter.model.PcProductInfoForI;
import com.cmall.productcenter.model.api.ApiGetProductsInput;
import com.cmall.productcenter.model.api.ApiGetProductsResult;
import com.cmall.productcenter.service.CategoryService;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiGetProducts extends RootApi<ApiGetProductsResult,ApiGetProductsInput> {

	public ApiGetProductsResult Process(ApiGetProductsInput api, MDataMap mRequestMap) {
		ApiGetProductsResult result = new ApiGetProductsResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			ProductService s = new ProductService();
			List<PcProductInfoForI> productList = s.getProductListForI(api.getProductStrs());
			result.setProductList(productList);
		}
		
		return result;
	}
}
