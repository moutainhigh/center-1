package com.cmall.newscenter.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 品牌 - 在售商品列表输出类
 * @author liqiang
 * date 2014-7-10
 * @version 1.0
 */
public class BrandProductInSaleResult extends RootResultWeb{
	
	@ZapcomApi(value = "在售商品")
	private List<Sale_Product> products = new ArrayList<Sale_Product>();

	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	

	public List<Sale_Product> getProducts() {
		return products;
	}

	public void setProducts(List<Sale_Product> products) {
		this.products = products;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}
}
