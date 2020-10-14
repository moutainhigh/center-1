package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetFSkuNowInput extends RootInput {
	
	@ZapcomApi(value = "品牌编号" ,demo= "467703130008000100060001",require = 1,verify={ "in=467703130008000100060001" })
	private String activity = "467703130008000100060001";

	public String getActivity() {
		return activity;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}
	
}
