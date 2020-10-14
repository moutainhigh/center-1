package com.cmall.ordercenter.model.api;

import java.util.List;

import com.cmall.ordercenter.model.OrderInfoForCC;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetOrdersForCCResult extends RootResult {
	
	@ZapcomApi(value="订单结果list")
	private List<OrderInfoForCC> list = null;

	public List<OrderInfoForCC> getList() {
		return list;
	}

	public void setList(List<OrderInfoForCC> list) {
		this.list = list;
	}
}
