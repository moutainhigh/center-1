package com.cmall.productcenter.service.api;

import com.cmall.productcenter.model.PcBrandinfo;
import com.cmall.productcenter.model.api.ApiGetBrandNameInput;
import com.cmall.productcenter.model.api.ApiGetBrandNameResult;
import com.cmall.productcenter.service.CategoryService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;


/**
 * @author zb
 *
 */
public class ApiGetBrandName extends RootApi<ApiGetBrandNameResult,ApiGetBrandNameInput> {

	@Override
	public ApiGetBrandNameResult Process(ApiGetBrandNameInput inputParam, MDataMap mRequestMap) {
		// TODO Auto-generated method stub
		ApiGetBrandNameResult result = new ApiGetBrandNameResult();
		CategoryService service = new CategoryService();
		PcBrandinfo brandInfo = service.getBrandById(inputParam.getBrandCode());
		result.setBrandInfo(brandInfo);
		return result;
	}

}
