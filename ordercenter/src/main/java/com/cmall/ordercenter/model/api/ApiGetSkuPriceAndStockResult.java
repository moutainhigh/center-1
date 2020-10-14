package com.cmall.ordercenter.model.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.ordercenter.model.SkuForCache;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetSkuPriceAndStockResult extends RootResult {
	
	private List<SkuForCache> list = new ArrayList<SkuForCache>();

	public List<SkuForCache> getList() {
		return list;
	}

	public void setList(List<SkuForCache> list) {
		this.list = list;
	}
	
	
	
}
