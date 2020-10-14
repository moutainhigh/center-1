package com.cmall.groupcenter.account.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetRelationForImResult extends RootResultWeb{
	
	@ZapcomApi(value = "信息列表")
	List<GetRelationForImInfo> infoList=new ArrayList<GetRelationForImInfo>();

	public List<GetRelationForImInfo> getInfoList() {
		return infoList;
	}

	public void setInfoList(List<GetRelationForImInfo> infoList) {
		this.infoList = infoList;
	}
}
