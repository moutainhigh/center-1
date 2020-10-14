package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.topapi.RootResult;

/**
 * 
 * @author 李国杰
 *
 */
public class ApiGetProductNameResult extends RootResult {

	 private String productName = null;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
}
