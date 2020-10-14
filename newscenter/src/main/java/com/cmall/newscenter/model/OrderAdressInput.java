package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 订单-配送地址输入类
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class OrderAdressInput extends RootInput{
	
	@ZapcomApi(value="订单序号")
	private String order="";

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
	
}
