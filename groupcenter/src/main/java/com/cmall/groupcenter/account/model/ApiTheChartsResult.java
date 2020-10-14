package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiTheChartsResult extends RootResultWeb {
	@ZapcomApi(value="信息列表",remark="信息列表")
	private List<ApiTheChartsResultList> list = new ArrayList<ApiTheChartsResultList>();

	public List<ApiTheChartsResultList> getList() {
		return list;
	}

	public void setList(List<ApiTheChartsResultList> list) {
		this.list = list;
	}
}