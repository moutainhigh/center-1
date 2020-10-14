package com.cmall.usercenter.model.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.usercenter.model.UcSellerInfoBaseInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetSellerNameResult extends RootResult {
	@ZapcomApi(value="商户信息列表",remark="")
	private List<UcSellerInfoBaseInfo> sellerList = new ArrayList<UcSellerInfoBaseInfo>();

	public List<UcSellerInfoBaseInfo> getSellerList() {
		return sellerList;
	}

	public void setSellerList(List<UcSellerInfoBaseInfo> sellerList) {
		this.sellerList = sellerList;
	}

	
}
