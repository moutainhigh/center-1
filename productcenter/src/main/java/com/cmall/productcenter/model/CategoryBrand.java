package com.cmall.productcenter.model;

import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class CategoryBrand {
	@ZapcomApi(value="分类编号")
	private List<String> categoryTwoId;
	@ZapcomApi(value="分类名称")
	private List<String> categoryTwoName;
	@ZapcomApi(value="品牌编号")
	private List<String> brandId;
	@ZapcomApi(value="品牌名称")
	private List<String> brandName;
	public List<String> getCategoryTwoId() {
		return categoryTwoId;
	}
	public void setCategoryTwoId(List<String> categoryTwoId) {
		this.categoryTwoId = categoryTwoId;
	}
	public List<String> getCategoryTwoName() {
		return categoryTwoName;
	}
	public void setCategoryTwoName(List<String> categoryTwoName) {
		this.categoryTwoName = categoryTwoName;
	}
	public List<String> getBrandId() {
		return brandId;
	}
	public void setBrandId(List<String> brandId) {
		this.brandId = brandId;
	}
	public List<String> getBrandName() {
		return brandName;
	}
	public void setBrandName(List<String> brandName) {
		this.brandName = brandName;
	}
	
}
