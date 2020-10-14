package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 热门搜索
 * @author shiyz
 * date 2014-7-18
 * @version 1.0
 */
public class PopularSearch {

	@ZapcomApi(value = "排行")
	private int order = 0;
	
	@ZapcomApi(value = "标题")
	private String title = "";
	
	@ZapcomApi(value = "上升或者下降名次")
	private int change = 0;

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getChange() {
		return change;
	}

	public void setChange(int change) {
		this.change = change;
	}



}
