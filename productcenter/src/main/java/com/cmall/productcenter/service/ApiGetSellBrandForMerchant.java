package com.cmall.productcenter.service;

import java.util.List;

import com.cmall.productcenter.model.ApiGetBrandResult;
import com.cmall.productcenter.model.ApiGetSellBrandInput;
import com.cmall.productcenter.model.PcBrandinfo;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

/**
 * 根据商户编号查询商户的品牌
 * @author lgx
 *
 */
public class ApiGetSellBrandForMerchant extends RootApi<ApiGetBrandResult, ApiGetSellBrandInput> {

	public ApiGetBrandResult Process(ApiGetSellBrandInput api,MDataMap mRequestMap) {
		ApiGetBrandResult result = new ApiGetBrandResult();
		CategoryService s = new CategoryService();
		String sellerCode = api.getSellerCode();
		
		List<PcBrandinfo> list = s.getBrandListForMerchant(sellerCode);
		result.setList(list);
		return result;
	}

}
