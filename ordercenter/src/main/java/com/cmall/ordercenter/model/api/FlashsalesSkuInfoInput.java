package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class FlashsalesSkuInfoInput extends RootInput {
	
	/**
	 * 活动编号
	 */
	@ZapcomApi(value="活动编号",require=1)
	private String activity_code = "";
	
	/**
	 * SKU编号
	 */
	@ZapcomApi(value="SKU编号",require=1)
	private String sku_code = "";

	public String getActivity_code() {
		return activity_code;
	}

	public void setActivity_code(String activity_code) {
		this.activity_code = activity_code;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	
}
