package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.topapi.RootResult;

/**
 * 
 * @author wangkecheng
 *
 */
public class ApiGetCategoryResultForCm  extends RootResult {

	private List<Category> list = new ArrayList<Category>();

	public List<Category> getList() {
		return list;
	}

	public void setList(List<Category> list) {
		this.list = list;
	}
	
}
