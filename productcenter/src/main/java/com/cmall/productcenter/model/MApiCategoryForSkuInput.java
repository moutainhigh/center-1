package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class MApiCategoryForSkuInput extends RootInput {

	
	/**
	 * 分类编号 默认传入2级分类  传三级分类时自动截取
	 */
	@ZapcomApi(value="分类编号")
	private String categoryCode="";

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	
	public String productCode = "";

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
	
}
