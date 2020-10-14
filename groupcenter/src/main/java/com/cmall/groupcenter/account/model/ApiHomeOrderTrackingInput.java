package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 
 * @author wz
 *
 */
public class ApiHomeOrderTrackingInput extends RootInput{
	@ZapcomApi(value="订单编号",require=1)
	private String order_code="";
	
	public String getOrder_code() {
		return order_code;
	}

	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}
	
	
}
