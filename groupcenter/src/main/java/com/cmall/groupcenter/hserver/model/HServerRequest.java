package com.cmall.groupcenter.hserver.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 请求公用类
 * @author jlin
 *
 * @param <T>
 */
public class HServerRequest<T> {

	private String moi_code_cd = "";
	private String push_time = "";
	private List<T> results =new ArrayList<T>();
	
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

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}

	
}
