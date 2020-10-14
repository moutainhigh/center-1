package com.cmall.productcenter.model;

public class ApiGetSellerCategoryInputForCm extends ApiGetCategoryInput {
	/**
	 * 商家编号
	 */
	private String sellerCode = "";

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}
	/**
	 * 对应值 为 1,2,3 一级分类的Pid，可以为空，一级分类的Pid可以为空。
	 */
	private int level = 0;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
	
}
