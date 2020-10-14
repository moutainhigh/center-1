package com.cmall.groupcenter.third.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GroupReconciliationResult extends RootResultWeb{
    
	@ZapcomApi(value = "对账表",remark = "详情列表")
	List<GroupReconciliationDetail> detailList=new ArrayList<GroupReconciliationDetail>();

	public List<GroupReconciliationDetail> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<GroupReconciliationDetail> detailList) {
		this.detailList = detailList;
	}
	
	
}
