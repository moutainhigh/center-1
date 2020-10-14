package com.cmall.ordercenter.model.api.reconciliation.result;

import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.topapi.RootResult;

public class ImportRedDashedResult extends RootResult {

	private List<Map<String, Object>> list;

	private String error;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

}
