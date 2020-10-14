package com.cmall.newscenter.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 品牌 - 试用商品列表输出类
 * @author liqiang
 * date 2014-7-10
 * @version 1.0
 */
public class BrandProductOnTryResult extends RootResultWeb{
	
	@ZapcomApi(value = "")
	private List<Trial_product> products = new ArrayList<Trial_product>();

	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	

	public List<Trial_product> getProducts() {
		return products;
	}

	public void setProducts(List<Trial_product> products) {
		this.products = products;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}
}
