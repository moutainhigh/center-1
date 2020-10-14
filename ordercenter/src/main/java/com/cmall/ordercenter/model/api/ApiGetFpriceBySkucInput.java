package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetFpriceBySkucInput extends RootInput {
	
	/**
	 * SKU编号
	 */
	@ZapcomApi(value="SKU编号",require=1,demo="2143321,43141,32141",remark="多个sku 编号以,分隔")
	private String sku_codes = "";

	public String getSku_codes() {
		return sku_codes;
	}

	public void setSku_codes(String sku_codes) {
		this.sku_codes = sku_codes;
	}
	
}
