package com.cmall.groupcenter.account.model;

import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetRelationInfoResult extends RootResultWeb{

	@ZapcomApi(value = "详细信息",remark="详细信息")
	List<GetRelationInfo> getRelationInfoList=null;

	public List<GetRelationInfo> getGetRelationInfoList() {
		return getRelationInfoList;
	}

	public void setGetRelationInfoList(List<GetRelationInfo> getRelationInfoList) {
		this.getRelationInfoList = getRelationInfoList;
	}
	
}
