package com.cmall.systemcenter.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.topapi.RootResult;

public class ChinaAreaResult extends RootResult{
	public List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
}