package com.cmall.productcenter.service.api;

import java.util.List;

import com.cmall.productcenter.model.PcProductInfoForI;
import com.cmall.productcenter.model.ProductSkuInfo;

import com.cmall.productcenter.model.api.ApiGetSkusInput;
import com.cmall.productcenter.model.api.ApiGetSkusResult;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiGetSkus extends RootApi<ApiGetSkusResult,ApiGetSkusInput> {

	public ApiGetSkusResult Process(ApiGetSkusInput api, MDataMap mRequestMap) {
		ApiGetSkusResult result = new ApiGetSkusResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			ProductService s = new ProductService();
			List<ProductSkuInfo> skuList = s.getSkuListForI(api.getSkuStrs());
			result.setSkuList(skuList);
		}
		
		return result;
	}
}

