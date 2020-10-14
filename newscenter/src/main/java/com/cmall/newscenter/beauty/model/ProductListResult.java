package com.cmall.newscenter.beauty.model;


import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 商品列表输出类
 * @author houwen
 * date 2014-8-28
 * @version 1.0
 */
public class ProductListResult extends RootResultWeb{
	
	@ZapcomApi(value = "商品LIST")
	private List<SaleProduct> products = new ArrayList<SaleProduct>();
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	public List<SaleProduct> getProducts() {
		return products;
	}

	public void setProducts(List<SaleProduct> products) {
		this.products = products;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

}
