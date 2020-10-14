package com.cmall.productcenter.service;

import com.cmall.productcenter.model.ApiGetProductInput;
import com.cmall.productcenter.model.ApiGetProductResult;
import com.cmall.productcenter.model.PcProductinfo;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiGetProduct extends
		RootApi<ApiGetProductResult, ApiGetProductInput> {

	private final static ProductService ps = new ProductService();

	public ApiGetProductResult Process(ApiGetProductInput api, MDataMap mRequestMap) {
		ApiGetProductResult aResut = new ApiGetProductResult();
		
		if(api == null)
		{
			aResut.setResultMessage(bInfo(941901019));
			aResut.setResultCode(941901019);
		}
		else if(api.getProductCode() == null || api.getProductCode().equals(""))
		{
			aResut.setResultMessage(bInfo(941901020));
			aResut.setResultCode(941901020);
		}
		else	
		{
			PcProductinfo product = ps.getProduct(api.getProductCode());
			
		
			
			if(product==null)
			{
				aResut.setResultMessage(bInfo(941901018, api.getProductCode()));
				aResut.setResultCode(941901018);
			}
			else{
				//取网站的商品
				if(api.getType() == 1){
					//非网站的商品并且是上架的，给予下架状态
					if(!product.getSaleScopeDid().equals("449746400001")){
						if(product.getProductStatus().equals("4497153900060002"))
							product.setProductStatus("4497153900060003");
					}
				}
			}
			
			aResut.setProductInfo(product);
			
		}
		
		return aResut;

	}

}
