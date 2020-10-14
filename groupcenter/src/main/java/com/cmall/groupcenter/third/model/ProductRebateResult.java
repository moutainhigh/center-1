package com.cmall.groupcenter.third.model;

import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ProductRebateResult extends RootResultWeb{
	
	@ZapcomApi(value = "返利比例列表")
	private List<ProductRebateInfo> rebateInfoList;

	public List<ProductRebateInfo> getRebateInfoList() {
		return rebateInfoList;
	}

	public void setRebateInfoList(List<ProductRebateInfo> rebateInfoList) {
		this.rebateInfoList = rebateInfoList;
	}
}
