package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 限时抢购  输出类
 * @author yangrong
 * date: 2014-09-17
 * @version1.0
 */
public class TimedScareBuyingListResult extends RootResultWeb{
	
	@ZapcomApi(value = "限时商品LIST")
	private List<TimedScareBuying> products = new ArrayList<TimedScareBuying>();
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	public List<TimedScareBuying> getProducts() {
		return products;
	}

	public void setProducts(List<TimedScareBuying> products) {
		this.products = products;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}
	
	

}
