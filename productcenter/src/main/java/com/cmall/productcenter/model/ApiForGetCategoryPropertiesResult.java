package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.topapi.RootResult;

public class ApiForGetCategoryPropertiesResult extends RootResult {

	private List<CategoryProperties> cpList = new ArrayList<CategoryProperties>();

	public List<CategoryProperties> getCpList() {
		return cpList;
	}

	public void setCpList(List<CategoryProperties> cpList) {
		this.cpList = cpList;
	}
	
}
