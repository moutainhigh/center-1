package com.cmall.productcenter.model;

public class Category {
	private String id;
	private String uid;
	private String categoryCode;
	private String categoryName;
	private String parentCode;
	private String sort; 
	private double cpsrate;
	public String getId() {
		return id;
	} 
	public double getCpsrate() {
		return cpsrate;
	}
	public void setCpsrate(double cpsrate) {
		this.cpsrate = cpsrate;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
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
