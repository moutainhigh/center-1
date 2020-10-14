package com.cmall.productcenter.model.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.ProductSkuInfo;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiChangeProductSkuInput  extends RootInput{
	public List<ProductSkuInfo> skuList = new ArrayList<ProductSkuInfo>();

	public List<ProductSkuInfo> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<ProductSkuInfo> skuList) {
		this.skuList = skuList;
	}
	
	
}
