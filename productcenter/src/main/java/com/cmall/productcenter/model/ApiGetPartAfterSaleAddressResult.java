package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetPartAfterSaleAddressResult extends RootResult {

	private List<OcAddressinfo> list = new ArrayList<OcAddressinfo>();

	public List<OcAddressinfo> getList() {
		return list;
	}

	public void setList(List<OcAddressinfo> list) {
		this.list = list;
	}
	
}
