package com.cmall.productcenter.service.api;

import java.util.List;

import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.model.VProductSku;
import com.cmall.productcenter.model.api.ApiGetVSkusInput;
import com.cmall.productcenter.model.api.ApiGetVSkusResult;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiGetVSkus extends RootApi<ApiGetVSkusResult,ApiGetVSkusInput>  {

	public ApiGetVSkusResult Process(ApiGetVSkusInput api,
			MDataMap mRequestMap) {
		ApiGetVSkusResult result = new ApiGetVSkusResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			ProductService s = new ProductService();
			List<VProductSku> skuList = s.getVSkuListForI(api.getSkuStrs());
			result.setSkuList(skuList);
		}
		
		return result;
	}

}
