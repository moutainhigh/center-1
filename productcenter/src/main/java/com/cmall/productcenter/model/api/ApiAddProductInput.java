package com.cmall.productcenter.model.api;

import com.cmall.productcenter.model.PcProductinfo;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiAddProductInput  extends RootInput{
	private PcProductinfo product = new PcProductinfo();

	public PcProductinfo getProduct() {
		return product;
	}

	public void setProduct(PcProductinfo product) {
		this.product = product;
	}
}
