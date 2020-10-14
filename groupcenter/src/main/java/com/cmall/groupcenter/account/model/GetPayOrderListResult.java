package com.cmall.groupcenter.account.model;

import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetPayOrderListResult extends RootResultWeb{

	@ZapcomApi(value = "提款单信息",remark="提款单信息")
	List<PayOrderInfo> getPayOrderInfoList=null;

	public List<PayOrderInfo> getGetPayOrderInfoList() {
		return getPayOrderInfoList;
	}

	public void setGetPayOrderInfoList(List<PayOrderInfo> getPayOrderInfoList) {
		this.getPayOrderInfoList = getPayOrderInfoList;
	}

	
}
