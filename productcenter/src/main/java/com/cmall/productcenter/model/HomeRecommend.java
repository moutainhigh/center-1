package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class HomeRecommend {
	@ZapcomApi(value="分类显示名称")
	private String categoryNote;
	@ZapcomApi(value="分类搜索名称")
	private String categoryName;
	@ZapcomApi(value="分类编号")
	private String categoryCode;
	public String getCategoryNote() {
		return categoryNote;
	}
	public void setCategoryNote(String categoryNote) {
		this.categoryNote = categoryNote;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	
}
