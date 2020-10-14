package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ShowTwoRelationResult extends RootResultWeb {

	@ZapcomApi(value = "二度社友明细")
	private List<RelationInfo> relationInfos = new ArrayList<RelationInfo>();
	
	@ZapcomApi(value = "翻页结果", remark = "翻页结果")
	PageResults pageResults=new PageResults();

	public PageResults getPageResults() {
		return pageResults;
	}

	public void setPageResults(PageResults pageResults) {
		this.pageResults = pageResults;
	}

	public List<RelationInfo> getRelationInfos() {
		return relationInfos;
	}

	public void setRelationInfos(List<RelationInfo> relationInfos) {
		this.relationInfos = relationInfos;
	}




}
