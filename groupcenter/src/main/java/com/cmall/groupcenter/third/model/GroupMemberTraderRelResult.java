package com.cmall.groupcenter.third.model;

import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GroupMemberTraderRelResult extends RootResultWeb{

	@ZapcomApi(value = "与店铺关系列表")
	private List<GroupMemberTraderRelDetail> relationList;

	public List<GroupMemberTraderRelDetail> getRelationList() {
		return relationList;
	}

	public void setRelationList(List<GroupMemberTraderRelDetail> relationList) {
		this.relationList = relationList;
	}

	
}
