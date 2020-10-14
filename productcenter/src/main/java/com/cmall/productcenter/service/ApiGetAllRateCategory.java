package com.cmall.productcenter.service;

import com.cmall.productcenter.model.ApiGetCategoryRateInput;
import com.cmall.productcenter.model.ApiGetCategoryRateResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiGetAllRateCategory extends RootApi<ApiGetCategoryRateResult, ApiGetCategoryRateInput> {


	
	public ApiGetCategoryRateResult Process(ApiGetCategoryRateInput api, MDataMap mRequestMap) {
		ApiGetCategoryRateResult result = new ApiGetCategoryRateResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else if(api.getCid() == null || api.getCid() == ""){
			result.setResultMessage(bInfo(941901023));
			result.setResultCode(941901023);
		}else{
			CategoryService s = new CategoryService();
			result.setCpsrate(s.getCategoryRate(api.getCid()));
		}
		
		return result;
	}

}
