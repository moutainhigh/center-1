package com.cmall.productcenter.model.api;

import com.cmall.productcenter.model.PcBrandinfo;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * 
 * @author zb
 *
 */
public class ApiGetBrandNameResult extends RootResult {

	 private PcBrandinfo brandInfo = new PcBrandinfo();

	public PcBrandinfo getBrandInfo() {
		return brandInfo;
	}

	public void setBrandInfo(PcBrandinfo brandInfo) {
		this.brandInfo = brandInfo;
	}
	 
	 
}
