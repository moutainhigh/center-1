package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 搜索商品  输出类
 * @author yangrong
 * date 2014-9-20
 * @version 1.0
 */
public class SearchProductsListResult extends RootResultWeb {
	
	@ZapcomApi(value = "商品列表")
	private List<SaleProduct> products = new ArrayList<SaleProduct>();

	public List<SaleProduct> getProducts() {
		return products;
	}

	public void setProducts(List<SaleProduct> products) {
		this.products = products;
	}

}
