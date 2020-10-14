package com.cmall.productcenter.service;

import java.util.List;

import com.cmall.productcenter.model.ApiGetCategoryInput;
import com.cmall.productcenter.model.ApiGetCategoryResult;
import com.cmall.productcenter.model.ApiGetProductResult;
import com.cmall.productcenter.model.Category;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiGetAllCategory extends RootApi<ApiGetCategoryResult, ApiGetCategoryInput> {

//	public ApiGetCategoryResult Process(ApiGetCategoryInput api) {
//		ApiGetCategoryResult result = new ApiGetCategoryResult();
//		
//		CategoryService s = new CategoryService();
//		List<Category> list = s.getAllCategory();
//		result.setList(list);
//		
//		return result;
//	}
	
	public ApiGetCategoryResult Process(ApiGetCategoryInput api, MDataMap mRequestMap) {
		ApiGetCategoryResult result = new ApiGetCategoryResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else if(api.getPid() == null || api.getPid() == ""){
			result.setResultMessage(bInfo(941901022));
			result.setResultCode(941901022);
		}else{
			CategoryService s = new CategoryService();
			List<Category> list = s._getAllCategory(api.getPid());
			result.setList(list);
		}
		
		return result;
	}

}
