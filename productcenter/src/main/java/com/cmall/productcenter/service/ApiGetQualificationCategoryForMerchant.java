package com.cmall.productcenter.service;

import java.util.List;

import com.cmall.productcenter.model.ApiGetQualificationCategoryInput;
import com.cmall.productcenter.model.ApiGetQualificationCategoryResult;
import com.cmall.productcenter.model.PcSellerQualification;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

/**
 * 根据商户编号查询商户有效的资质品类
 * @author lgx
 *
 */
public class ApiGetQualificationCategoryForMerchant extends RootApi<ApiGetQualificationCategoryResult, ApiGetQualificationCategoryInput> {

	public ApiGetQualificationCategoryResult Process(ApiGetQualificationCategoryInput api,MDataMap mRequestMap) {
		ApiGetQualificationCategoryResult result = new ApiGetQualificationCategoryResult();
		CategoryService s = new CategoryService();
		String sellerCode = api.getSellerCode();
		
		List<PcSellerQualification> list = s.getQualificationCategoryForMerchant(sellerCode);
		result.setList(list);
		return result;
	}

}
