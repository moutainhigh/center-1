package com.cmall.productcenter.model.api;

import java.util.List;

import com.cmall.productcenter.model.ProductSkuInfo;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetSkusResult extends RootResult {
	
	private List<ProductSkuInfo> skuList = null;

	public List<ProductSkuInfo> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<ProductSkuInfo> skuList) {
		this.skuList = skuList;
	}
	
	

}
