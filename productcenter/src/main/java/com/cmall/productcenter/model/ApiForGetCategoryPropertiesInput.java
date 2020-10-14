package com.cmall.productcenter.model;

import com.srnpr.zapcom.topapi.RootInput;

public class ApiForGetCategoryPropertiesInput extends RootInput {

	private String categoryCode;

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	
	
}
