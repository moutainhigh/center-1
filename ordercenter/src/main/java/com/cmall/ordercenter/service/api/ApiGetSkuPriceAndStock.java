package com.cmall.ordercenter.service.api;

import java.util.List;

import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.api.ApiGetSkuPriceAndStockInput;
import com.cmall.ordercenter.model.api.ApiGetSkuPriceAndStockResult;
import com.cmall.ordercenter.service.OrderService;
import com.cmall.ordercenter.service.cache.ProductCacheManage;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiGetSkuPriceAndStock extends RootApi<ApiGetSkuPriceAndStockResult,ApiGetSkuPriceAndStockInput> {

	public ApiGetSkuPriceAndStockResult Process(ApiGetSkuPriceAndStockInput api, MDataMap mRequestMap) {
		ApiGetSkuPriceAndStockResult result = new ApiGetSkuPriceAndStockResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			ProductCacheManage pcm = new ProductCacheManage();
			result.setList(pcm.getSkuForCacheList(api.getSkuStrs()));
			result.setResultCode(1);
			result.setResultMessage("");
			
		}
		
		return result;
	}
}
