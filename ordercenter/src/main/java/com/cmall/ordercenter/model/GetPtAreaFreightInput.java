package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 获得商品到某区域的运费
 * @author huoqiangshou
 *
 */
public class GetPtAreaFreightInput extends RootInput{
	
	/**
	 * 商品编码
	 */
	@ZapcomApi(value="商品编码")
	private String productCode;

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	
	
}
