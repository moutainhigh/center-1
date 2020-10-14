package com.cmall.productcenter.model;

import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetProductResult extends RootResult {

	
	/**
	 * 返回的商品信息
	 */
	private PcProductinfo productInfo=new PcProductinfo();

	public PcProductinfo getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(PcProductinfo productInfo) {
		this.productInfo = productInfo;
	}
	
	
}
