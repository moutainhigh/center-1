package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetQualificationCategoryResult extends RootResult {

	private List<PcSellerQualification> list = new ArrayList<PcSellerQualification>();

	public List<PcSellerQualification> getList() {
		return list;
	}

	public void setList(List<PcSellerQualification> list) {
		this.list = list;
	}
	
	
}
