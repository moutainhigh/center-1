package com.cmall.usercenter.model.api;

import com.cmall.usercenter.model.UcSellerInfo;
import com.srnpr.zapcom.topapi.RootResult;


public class ApiGetSellerInfoResult extends RootResult {

	private UcSellerInfo sellerInfo = new UcSellerInfo();

	public UcSellerInfo getSellerInfo() {
		return sellerInfo;
	}

	public void setSellerInfo(UcSellerInfo sellerInfo) {
		this.sellerInfo = sellerInfo;
	}
	
	
}
