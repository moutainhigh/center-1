package com.cmall.newscenter.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 订单-已试用订单输出类
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class OrderListTrialResult extends RootResultWeb{
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	@ZapcomApi("已试用订单信息")
	private List<TrialOrder> orders = new ArrayList<TrialOrder>();

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	public List<TrialOrder> getOrders() {
		return orders;
	}

	public void setOrders(List<TrialOrder> orders) {
		this.orders = orders;
	}
}
