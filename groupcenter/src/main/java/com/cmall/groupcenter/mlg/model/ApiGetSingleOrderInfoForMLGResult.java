package com.cmall.groupcenter.mlg.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiGetSingleOrderInfoForMLGResult  extends RootResultWeb {

	@ZapcomApi(value = "订单信息", remark = "")
	OrderBaseInfo data = new OrderBaseInfo();

	public OrderBaseInfo getData() {
		return data;
	}

	public void setData(OrderBaseInfo data) {
		this.data = data;
	}
	
}
