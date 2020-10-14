package com.cmall.productcenter.service.api;

import com.cmall.productcenter.model.api.ApiGetProductForFlashInput;
import com.cmall.productcenter.model.api.ApiGetSkuinfoResult;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuQuery;
import com.srnpr.xmassystem.service.ProductPriceService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

/**
 * 添加闪购的商品信息
 * @author jlin
 *
 */
public class ApiGetSkuinfoByProductCode extends RootApi<ApiGetSkuinfoResult,ApiGetProductForFlashInput> {
	
	public ApiGetSkuinfoResult Process(ApiGetProductForFlashInput input, MDataMap mRequestMap) {
		
		ApiGetSkuinfoResult result = new ApiGetSkuinfoResult();
		String product_code=input.getProduct_code();
		
		PlusModelSkuQuery skuQuery = new PlusModelSkuQuery();
		skuQuery.setCode(product_code);
		PlusModelSkuInfo plusModelSkuInfo = new ProductPriceService().getProductMinPriceSkuInfo(skuQuery).get(product_code);
		result.setInfo(plusModelSkuInfo);
		
		return result;
	}
}
