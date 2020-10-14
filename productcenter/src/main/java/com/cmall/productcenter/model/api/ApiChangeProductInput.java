package com.cmall.productcenter.model.api;

import com.cmall.productcenter.model.PcProductinfo;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiChangeProductInput  extends RootInput{
	
	private PcProductinfo product = null;

	public PcProductinfo getProduct() {
		return product;
	}

	public void setProduct(PcProductinfo product) {
		this.product = product;
	}
}
