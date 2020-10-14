package com.cmall.ordercenter.model.api;

import java.util.List;

import com.cmall.ordercenter.model.Express;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetExpResult extends RootResult {

	private List<Express> list = null;

	public List<Express> getList() {
		return list;
	}

	public void setList(List<Express> list) {
		this.list = list;
	}
}
