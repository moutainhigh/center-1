package com.cmall.groupcenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.basemodel.MDataMap;

public class MPageData {

	private PageResults pageResults = new PageResults();
	
	private List<MDataMap> listData = new ArrayList<MDataMap>();

	public PageResults getPageResults() {
		return pageResults;
	}

	public void setPageResults(PageResults pageResults) {
		this.pageResults = pageResults;
	}

	public List<MDataMap> getListData() {
		return listData;
	}

	public void setListData(List<MDataMap> listData) {
		this.listData = listData;
	}

	

}
