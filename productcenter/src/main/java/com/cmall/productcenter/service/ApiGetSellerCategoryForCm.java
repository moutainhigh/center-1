package com.cmall.productcenter.service;

import java.util.List;

import com.cmall.productcenter.model.ApiGetCategoryResultForCm;
import com.cmall.productcenter.model.ApiGetSellerCategoryInputForCm;
import com.cmall.productcenter.model.Category;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiGetSellerCategoryForCm  extends RootApi<ApiGetCategoryResultForCm, ApiGetSellerCategoryInputForCm> {
	
	public ApiGetCategoryResultForCm Process(ApiGetSellerCategoryInputForCm api, MDataMap mRequestMap) {
		ApiGetCategoryResultForCm result = new ApiGetCategoryResultForCm();
		
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			CategoryService s = new CategoryService();
			List<Category> list = s.getCategoryListForCm( api.getLevel(), api.getPid());
			result.setList(list);
		}
		
		return result;
	}
}
