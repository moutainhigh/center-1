package com.cmall.ordercenter.model.api;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;

public class AccountResult extends RootResult {

	private List<MDataMap> list = new ArrayList<MDataMap>();

	public List<MDataMap> getList() {
		return list;
	}

	public void setList(List<MDataMap> list) {
		this.list = list;
	}
	
}
