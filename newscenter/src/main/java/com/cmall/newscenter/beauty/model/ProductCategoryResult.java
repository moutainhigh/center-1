package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 惠美丽_商品分类输出类Api
 * @author yangrong
 * date: 2014-09-11
 * @version1.0
 */
public class ProductCategoryResult extends RootResultWeb{
	
	@ZapcomApi(value = "分类列表")
	private List<ProductCategory> categories = new ArrayList<ProductCategory>();
	
	@ZapcomApi(value = "排序列表")
	private List<Sort> sort = new ArrayList<Sort>();

	public List<ProductCategory> getCategories() {
		return categories;
	}

	public void setCategories(List<ProductCategory> categories) {
		this.categories = categories;
	}

	public List<Sort> getSort() {
		return sort;
	}

	public void setSort(List<Sort> sort) {
		this.sort = sort;
	}
	
	
	
}
