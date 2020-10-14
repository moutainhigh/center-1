package com.cmall.productcenter.service;

import java.util.List;

import com.cmall.productcenter.model.ApiGetQualificationCategoryInput;
import com.cmall.productcenter.model.ApiGetQualificationCategoryResult;
import com.cmall.productcenter.model.PcSellerQualification;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

/**
 * 查询商户所有资质品类
 * @author lgx
 *
 */
public class ApiGetAllQualificationCategory extends RootApi<ApiGetQualificationCategoryResult, ApiGetQualificationCategoryInput> {

	public ApiGetQualificationCategoryResult Process(ApiGetQualificationCategoryInput api,MDataMap mRequestMap) {
		ApiGetQualificationCategoryResult result = new ApiGetQualificationCategoryResult();
		CategoryService s = new CategoryService();
		
		List<PcSellerQualification> list = s.getAllQualificationCategory();
		result.setList(list);
		return result;
	}

}
