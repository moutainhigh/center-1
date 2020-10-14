package com.cmall.groupcenter.hserver.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.hserver.IHServerRequest;

public class FuncOrderStatusRequest implements IHServerRequest {

	private String moi_code_cd = "";
	private String push_time = "";
	
	private List<OrderStatus> results = new ArrayList<OrderStatus>();

	public String getMoi_code_cd() {
		return moi_code_cd;
	}

	public void setMoi_code_cd(String moi_code_cd) {
		this.moi_code_cd = moi_code_cd;
	}

	public String getPush_time() {
		return push_time;
	}

	public void setPush_time(String push_time) {
		this.push_time = push_time;
	}

	public List<OrderStatus> getResults() {
		return results;
	}

	public void setResults(List<OrderStatus> results) {
		this.results = results;
	}

	

}
