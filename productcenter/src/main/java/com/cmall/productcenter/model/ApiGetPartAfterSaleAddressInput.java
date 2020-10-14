package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetPartAfterSaleAddressInput extends RootInput {

	@ZapcomApi(value="商户售后地址uid")
	private String afterSaleAddressUid;
	
	@ZapcomApi(value="商户code")
	private String manageCode;

	public String getAfterSaleAddressUid() {
		return afterSaleAddressUid;
	}

	public void setAfterSaleAddressUid(String afterSaleAddressUid) {
		this.afterSaleAddressUid = afterSaleAddressUid;
	}

	public String getManageCode() {
		return manageCode;
	}

	public void setManageCode(String manageCode) {
		this.manageCode = manageCode;
	}
	
}
