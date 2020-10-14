package com.cmall.productcenter.model.api;

import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetSkuinfoResult extends RootResult {

	@ZapcomApi(value="sku信息")
	private PlusModelSkuInfo info;

	public PlusModelSkuInfo getInfo() {
		return info;
	}

	public void setInfo(PlusModelSkuInfo info) {
		this.info = info;
	}
	
	
}
