package com.cmall.newscenter.model;

import com.cmall.groupcenter.func.OrderInformation;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiCallSupportInput extends RootInput {
	
	@ZapcomApi(value="输入参数")
	OrderInformation  order = new OrderInformation();

	public OrderInformation getOrder() {
		return order;
	}

	public void setOrder(OrderInformation order) {
		this.order = order;
	} 
	
}
