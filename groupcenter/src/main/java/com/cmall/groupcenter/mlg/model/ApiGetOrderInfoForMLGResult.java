package com.cmall.groupcenter.mlg.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiGetOrderInfoForMLGResult  extends RootResultWeb {

	@ZapcomApi(value = "总页数", remark = "")
	int page = 0;
	
	@ZapcomApi(value = "总记录数", remark = "")
	int total = 0;
	
	@ZapcomApi(value = "订单列表", remark = "")
	List<OrderBaseInfo> list = new ArrayList<OrderBaseInfo>();

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<OrderBaseInfo> getList() {
		return list;
	}

	public void setList(List<OrderBaseInfo> list) {
		this.list = list;
	}

	
}
