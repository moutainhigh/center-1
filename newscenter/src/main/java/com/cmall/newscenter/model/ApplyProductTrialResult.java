package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 试用商品在线下单输入类
 * @author shiyz
 * date 2014-8-26
 */
public class ApplyProductTrialResult extends RootResultWeb {

	@ZapcomApi(value = "订单详情")
	SaleOrder saleOrder = new SaleOrder();

	public SaleOrder getSaleOrder() {
		return saleOrder;
	}

	public void setSaleOrder(SaleOrder saleOrder) {
		this.saleOrder = saleOrder;
	}


	
}
