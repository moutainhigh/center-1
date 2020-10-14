package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class CategoryBaseInfo {
	@ZapcomApi(value="分类编号")
	private String categoryCode = "";
	@ZapcomApi(value="分类名称")
	private String categoryName = "";
	@ZapcomApi(value="父分类")
	private String parentCode = "";
	@ZapcomApi(value="分类排序")
	private String sort = ""; 
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
}
