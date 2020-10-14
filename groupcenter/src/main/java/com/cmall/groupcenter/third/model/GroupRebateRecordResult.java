package com.cmall.groupcenter.third.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 第三方返利对账
 * @author chenbin
 *
 */
public class GroupRebateRecordResult extends RootResultWeb{

	@ZapcomApi(value = "返利对账表",remark = "返利详情列表")
	List<GroupRebateRecordList> rebateRecordList=new ArrayList<GroupRebateRecordList>();

	public List<GroupRebateRecordList> getRebateRecordList() {
		return rebateRecordList;
	}

	public void setRebateRecordList(List<GroupRebateRecordList> rebateRecordList) {
		this.rebateRecordList = rebateRecordList;
	}
}
