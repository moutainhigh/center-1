package com.cmall.productcenter.service;

import java.util.List;

import com.cmall.productcenter.model.ApiGetBrandResult;
import com.cmall.productcenter.model.ApiGetSellBrandInput;
import com.cmall.productcenter.model.PcBrandinfo;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiGetSellBrand extends RootApi<ApiGetBrandResult, ApiGetSellBrandInput> {

	public ApiGetBrandResult Process(ApiGetSellBrandInput api,MDataMap mRequestMap) {
		ApiGetBrandResult result = new ApiGetBrandResult();
		CategoryService s = new CategoryService();
		List<PcBrandinfo> list = s.getBrandList();
		result.setList(list);
		return result;
	}

}
