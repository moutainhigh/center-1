package com.cmall.usercenter.model.api;

import com.cmall.usercenter.model.SellerDesc;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetSellerDescResult  extends RootResult{
	private SellerDesc sellerDesc;

	public SellerDesc getSellerDesc() {
		return sellerDesc;
	}

	public void setSellerDesc(SellerDesc sellerDesc) {
		this.sellerDesc = sellerDesc;
	}
}
