package com.cmall.groupcenter.homehas.model;

import java.util.List;
import java.util.Map;

import com.cmall.groupcenter.groupface.IRsyncResponse;

public class RsyncGetCustInComingLineInfoRespone implements IRsyncResponse {
	private boolean success;
	
	private List<Map<String,String>> resultList;

	public List<Map<String, String>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, String>> resultList) {
		this.resultList = resultList;
	}
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
