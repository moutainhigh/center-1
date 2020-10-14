package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 品牌 - 在售商品列表输入类
 * @author liqiang
 * date 2014-7-10
 * @version 1.0
 */
public class GetSkuInfoInput extends RootInput {
	
	
	@ZapcomApi(value = "商品编号",remark="9108956")
	private String productCode = "";

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	
}
