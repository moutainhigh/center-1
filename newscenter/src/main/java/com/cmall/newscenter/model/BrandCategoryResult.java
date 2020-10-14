package com.cmall.newscenter.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 品牌 - 分类列表输出类
 * @author liqiang
 * date 2014-7-10
 * @version 1.0
 */
public class BrandCategoryResult extends RootResultWeb{
	
	@ZapcomApi(value = "分类列表")
	private List<Product_Category> categories = new ArrayList<Product_Category>();

	public List<Product_Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Product_Category> categories) {
		this.categories = categories;
	}

	
	
	
}
