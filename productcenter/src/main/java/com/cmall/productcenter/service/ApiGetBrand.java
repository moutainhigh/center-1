package com.cmall.productcenter.service;

import java.util.List;

import com.cmall.productcenter.model.ApiGetBrandInput;
import com.cmall.productcenter.model.ApiGetBrandResult;
import com.cmall.productcenter.model.PcBrandinfo;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiGetBrand extends RootApi<ApiGetBrandResult, ApiGetBrandInput> {

	
	
	public ApiGetBrandResult Process(ApiGetBrandInput api, MDataMap mRequestMap) { 
		ApiGetBrandResult result = new ApiGetBrandResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else if(api.getPid() == null || api.getPid() == ""){
			result.setResultMessage(bInfo(941901022));
			result.setResultCode(941901022);
		}else{
			CategoryService s = new CategoryService();
			List<PcBrandinfo> list = s.getBrand(api.getPid());
			result.setList(list);
		}
		
		return result;
	}

}
