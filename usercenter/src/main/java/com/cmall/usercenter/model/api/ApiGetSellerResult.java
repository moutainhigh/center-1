package com.cmall.usercenter.model.api;

import java.util.List;

import com.cmall.usercenter.model.CollectionSellerModel;
import com.cmall.usercenter.model.UcSellerInfo;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetSellerResult extends RootResult {
	
	private List<UcSellerInfo> sellerInfoList = null;

	public List<UcSellerInfo> getSellerInfoList() {
		return sellerInfoList;
	}

	public void setSellerInfoList(List<UcSellerInfo> sellerInfoList) {
		this.sellerInfoList = sellerInfoList;
	}
	
}
