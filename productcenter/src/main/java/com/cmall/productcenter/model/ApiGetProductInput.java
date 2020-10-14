package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetProductInput extends RootInput {

	/**
	 * 商品编号
	 */
	@ZapcomApi(value = "商品编号", remark = "网站商品的编号",require=1)
	private String productCode = "";

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
	 * 0:默认所有商品，1 网站商品，非网站商品为下架
	 */
	@ZapcomApi(value = "销售限制范围", remark = "0:默认所有商品，1 网站销售商品")
	private int type = 0;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

}
