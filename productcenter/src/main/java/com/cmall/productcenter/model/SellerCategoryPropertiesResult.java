package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.topapi.RootResult;


public class SellerCategoryPropertiesResult extends RootResult {

	/**
	 * 属性列表
	 */
	private List<Map<String, Object>> listProperty = new ArrayList<Map<String, Object>>();

	public List<Map<String, Object>> getListProperty() {
		return listProperty;
	}

	public void setListProperty(List<Map<String, Object>> listProperty) {
		this.listProperty = listProperty;
	}


}
