package com.cmall.usercenter.model.api;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;


public class ApiGetSellerInfoListResult extends RootResult {
	@ZapcomApi(value="商户信息")
	private List<MDataMap> sellerList = new ArrayList<MDataMap>();

	public List<MDataMap> getSellerList() {
		return sellerList;
	}

	public void setSellerList(List<MDataMap> sellerList) {
		this.sellerList = sellerList;
	}

}
