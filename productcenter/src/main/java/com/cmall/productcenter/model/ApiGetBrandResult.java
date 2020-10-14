package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.topapi.RootResult;

/**
 * 
 * @author wangkecheng
 *
 */
public class ApiGetBrandResult  extends RootResult {

	private List<PcBrandinfo> list = new ArrayList<PcBrandinfo>();

	public List<PcBrandinfo> getList() {
		return list;
	}

	public void setList(List<PcBrandinfo> list) {
		this.list = list;
	}
	
}
