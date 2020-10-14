package com.cmall.newscenter.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 订单-已支付订单输出类
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class OrderListPaidResult extends RootResultWeb{
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	@ZapcomApi("已支付订单信息")
	private List<SaleOrder> orders = new ArrayList<SaleOrder>();

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	public List<SaleOrder> getOrders() {
		return orders;
	}

	public void setOrders(List<SaleOrder> orders) {
		this.orders = orders;
	}

}
