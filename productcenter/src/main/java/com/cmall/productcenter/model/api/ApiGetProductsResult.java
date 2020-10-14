package com.cmall.productcenter.model.api;

import java.util.List;

import com.cmall.productcenter.model.PcProductInfoForI;
import com.cmall.productcenter.model.PcProductinfo;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetProductsResult extends RootResult {

	 private List<PcProductInfoForI> productList = null;

	public List<PcProductInfoForI> getProductList() {
		return productList;
	}

	public void setProductList(List<PcProductInfoForI> productList) {
		this.productList = productList;
	}
	 
	 
	 
}
