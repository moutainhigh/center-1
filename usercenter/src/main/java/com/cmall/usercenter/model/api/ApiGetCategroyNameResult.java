package com.cmall.usercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiGetCategroyNameResult extends RootResult {
	@ZapcomApi(value="分类名称",remark="总分类->分类1->分类11,总分类->分类1->分类12")
	private String categoryName = "";

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
}
