package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.topapi.RootResult;

/**
 * ClassName:商品虚类管理
 * @version 1.0
 * @author shiyz
 * Date: 2014-06-26
 */
public class AppCategoryResult extends RootResult {
	
	public List<List<String>> list = new ArrayList<List<String>>();
	

	public List<List<String>> getList() {
		return list;
	}

	public void setList(List<List<String>> list) {
		this.list = list;
	}
	
	

}
