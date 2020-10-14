package com.cmall.ordercenter.model.api.reconciliation.result;

import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.topapi.RootResult;

public class ImportReconciliationResult extends RootResult {

	private Integer errorSum = 0;

	private List<Map<String, Object>> list;

	private List<Map<String, Object>> errors;

	public List<Map<String, Object>> getErrors() {
		return errors;
	}

	public void setErrors(List<Map<String, Object>> errors) {
		this.errors = errors;
	}

	public Integer getErrorSum() {
		return errorSum;
	}

	public void setErrorSum(Integer errorSum) {
		this.errorSum = errorSum;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

}
