package com.cmall.productcenter.model.api;

import java.util.List;


import com.cmall.productcenter.model.VProductSku;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetVSkusResult extends RootResult {
	private List<VProductSku> skuList = null;

	public List<VProductSku> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<VProductSku> skuList) {
		this.skuList = skuList;
	}
	
	
}
