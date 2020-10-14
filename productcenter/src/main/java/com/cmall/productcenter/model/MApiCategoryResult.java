package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;

public class MApiCategoryResult extends RootResult {

	
	/**
	 * 属性列表
	 */
	private List<MDataMap> listProperty=new ArrayList<MDataMap>();

	public List<MDataMap> getListProperty() {
		return listProperty;
	}

	public void setListProperty(List<MDataMap> listProperty) {
		this.listProperty = listProperty;
	}
	
	
}
